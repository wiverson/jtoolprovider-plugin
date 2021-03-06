package io.github.wiverson;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.FileUtils;
import org.moditect.commands.AddModuleInfo;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

@Mojo(name = "collect-modules", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ModuleUtilities extends AbstractMojo {

    public final String GENERATE_INFO = "info";
    public final String GENERATE_OPEN = "open";
    private Log logger;
    @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
    private List<String> compilePath;
    @Parameter(required = true, defaultValue = "${project.build.directory}/module-info-work")
    private File moduleInfoWorkDirectory;
    /**
     * Where to place the transitive Maven dependencies that ARE packaged as modules
     */
    @Parameter(required = true, defaultValue = "${project.build.directory}/declared-modules")
    private File foundModulesDirectory;
    /**
     * Where to place the transitive Maven dependencies that are not packaged as modules
     */
    @Parameter(required = true, defaultValue = "${project.build.directory}/declared-not-modules")
    private File notModulesDirectory;
    /**
     * The directories for provided modules (e.g. JavaFX jmods)
     */
    @Parameter
    private List<File> providedModuleDirectories;
    @Parameter(alias = "ignoreJars")
    private List<String> ignoreJars;
    @Parameter(alias = "stripJars")
    private List<String> stripJars;
    @Parameter
    private int javaVersion = Runtime.version().feature();
    @Parameter
    private boolean debug;
    @Parameter(name = "autoClean", defaultValue = "true")
    private boolean autoClean;
    @Parameter
    private Properties moduleInfoOverrides;
    @Parameter(defaultValue = "false")
    private boolean skip = false;

    @Override
    public void setLog(org.apache.maven.plugin.logging.Log log) {
        this.logger = log;
    }

    private boolean isModule(JarFile jar) {

        // If it has a module-info.class in the root, it's a module
        ZipEntry zipEntry = jar.getEntry("module-info.class");
        if (zipEntry != null)
            return true;

        // If it's a multi-release, might be a module
        zipEntry = jar.getEntry("META-INF/versions");

        // Nope, not a multi-release, so it's not a module
        if (zipEntry == null)
            return false;

        for (int i = 8; i < javaVersion; i++) {
            zipEntry = jar.getEntry("META-INF/versions/" + i + "/module-info.class");
            if (zipEntry != null)
                return true;
        }

        return false;
    }

    private void stripModuleInfo(File jarFile) throws IOException {
        if (debug)
            logger.info("Removing module-info...");
        Map<String, String> env = new HashMap<>();
        env.put("create", "false");

        URI fileUri = jarFile.toURI();

        URI uri = URI.create("jar:" + fileUri.toString()); // Zip file path

        List<String> removeList = new ArrayList<>();
        removeList.add("module-info.class");
        for (int i = 8; i < javaVersion; i++) {
            removeList.add("META-INF/versions/" + i + "/module-info.class");
        }

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            for (String s : removeList) {
                if (Files.exists(zipfs.getPath(s))) {
                    Files.delete(zipfs.getPath(s)); // File inside zip to delete
                    if (debug)
                        logger.info("Removed " + s);
                }
            }
        }
    }

    private void generateModuleInfo(File jarFile) throws MojoExecutionException, IOException {

        String provided = isProvided(jarFile);

        if (provided != null) {
            String basePath =
                    moduleInfoWorkDirectory.getAbsolutePath() + File.separatorChar +
                            jarFile.getName().replace(".jar", "");
            basePath = basePath + "copied" + File.separatorChar + "versions" + File.separatorChar + javaVersion + File.separatorChar;
            FileUtils.copyFileToDirectory(provided, basePath);
            if (debug)
                logger.info("Copied " + provided + " to " + basePath);
            return;
        }

        RunTool runTool = new RunTool(getLog(), debug, debug, true);

        List<String> arguments = new ArrayList<>();
        arguments.add("--ignore-missing-deps");
        arguments.add("--add-modules=ALL-MODULE-PATH");
        arguments.add("--multi-release");
        arguments.add(Integer.toString(javaVersion));
        arguments.add("--module-path");
        arguments.add(buildModulesDirectory());

        if (isGenerated(jarFile))
            arguments.add("--generate-module-info");
        else
            arguments.add("--generate-open-module");

        arguments.add(moduleInfoWorkDirectory.getAbsolutePath() + File.separatorChar + jarFile.getName().replace(".jar", ""));
        arguments.add(jarFile.getAbsolutePath());

        if (debug) {
            for (String s : arguments)
                logger.info(s);
        }
        runTool.runTool("jdeps", arguments, true);
    }

    private boolean isGenerated(File jarFile) {
        for (Object key : moduleInfoOverrides.keySet()) {
            String keyString = key.toString();
            if (jarFile.getName().contains(keyString))
                if (GENERATE_INFO.compareToIgnoreCase(moduleInfoOverrides.get(key).toString()) == 0)
                    return true;
        }
        return false;
    }

    private String isProvided(File jarFile) {
        for (Object key : moduleInfoOverrides.keySet()) {
            String keyString = key.toString();
            if (jarFile.getName().contains(keyString)) {
                if (GENERATE_INFO.compareToIgnoreCase(moduleInfoOverrides.get(key).toString()) == 0)
                    return null;
                if (GENERATE_OPEN.compareToIgnoreCase(moduleInfoOverrides.get(key).toString()) == 0)
                    return null;

                return moduleInfoOverrides.get(key).toString();
            }
        }
        return null;
    }

    private void addModuleInfo(File jarFile) throws IOException {
        AddModuleInfo addModuleInfo = new AddModuleInfo(
                findModInfo(jarFile),
                null,
                Integer.toString(1),
                jarFile.toPath(),
                notModulesDirectory.toPath(),
                null,
                true);
        addModuleInfo.run();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (skip)
            return;

        if (autoClean)
            cleanUp();

        List<File> needsModuleInfo = new ArrayList<>();

        checkCustomProperties();

        int foundModules = 0;
        int foundWithoutModules = 0;
        int strippedModules = 0;

        for (String s : compilePath) {
            File entry = new File(s);
            if (!entry.isDirectory())
                try {
                    JarFile jarFile = new JarFile(entry);
                    if (!matches(jarFile, ignoreJars, "ignoreJars"))
                        if (isModule(jarFile)) {
                            if (debug)
                                logger.info(s + " IS a module");
                            foundModules++;
                            FileUtils.copyFileToDirectory(entry, foundModulesDirectory);
                            if (matches(jarFile, stripJars, "stripJars")) {
                                File newFile = new File(foundModulesDirectory, entry.getName());
                                strippedModules++;
                                if (debug)
                                    logger.info("Stripping info from " + newFile.getAbsolutePath());
                                stripModuleInfo(newFile);
                                needsModuleInfo.add(newFile);
                            } else {
                                if (debug)
                                    logger.info(jarFile.getName() + " didn't match strip");
                            }
                        } else {
                            if (debug)
                                logger.info(s + " is NOT a module, generating module info");
                            FileUtils.copyFileToDirectory(entry, notModulesDirectory);
                            File added = new File(notModulesDirectory, entry.getName());
                            foundWithoutModules++;
                            needsModuleInfo.add(added);
                        }
                } catch (IOException e) {
                    logger.error(e);
                    throw new MojoFailureException(e.getMessage());
                }
        }

        for (File jar : needsModuleInfo) {
            try {
                if (debug)
                    logger.info("Generating info for " + jar.getName());
                generateModuleInfo(jar);
            } catch (IOException e) {
                logger.error(e);
                throw new MojoFailureException(e.getMessage());
            }
        }

        for (File jar : needsModuleInfo) {
            try {
                if (debug)
                    logger.info("Adding info for " + jar.getName());
                addModuleInfo(jar);
            } catch (IOException e) {
                logger.error(e);
                throw new MojoFailureException(e.getMessage());
            }
        }

        logger.info("Found " + foundModules + " modular jars (stripped " + strippedModules + ") and " + foundWithoutModules + " ordinary jars.");
    }


    private void checkCustomProperties() throws MojoFailureException {
        if (moduleInfoOverrides == null)
            moduleInfoOverrides = new Properties();

        for (Object key : moduleInfoOverrides.keySet()) {
            Object value = moduleInfoOverrides.get(key);

            if (value == null)
                throw new MojoFailureException("Set " + key.toString() + " but missing value");

            String valueString = value.toString();
            if (GENERATE_INFO.compareToIgnoreCase(valueString) == 0)
                break;
            if (GENERATE_OPEN.compareToIgnoreCase(valueString) == 0)
                break;

            File file = new File(valueString);
            if (file.exists() && file.isFile() && file.getName().compareToIgnoreCase("module-info.java") == 0)
                break;
            throw new MojoFailureException("Set " + key.toString() + " but value should be [" + GENERATE_OPEN + "," +
                    GENERATE_INFO + "] or a valid path to a module-info.java file.");
        }
    }

    private void cleanUp() throws MojoFailureException {
        List<File> toClean = List.of(
                moduleInfoWorkDirectory,
                foundModulesDirectory,
                notModulesDirectory);

        for (File clean : toClean) {
            try {
                if (clean.exists()) {
                    Files.walk(clean.toPath())
                            .map(Path::toFile)
                            .sorted((o1, o2) -> -o1.compareTo(o2))
                            .forEach(File::delete);
                    clean.mkdirs();
                    if (debug)
                        logger.info("Reset directory " + clean.getAbsolutePath());
                }
            } catch (IOException ioException) {
                logger.error(ioException);
                throw new MojoFailureException(ioException.getMessage());
            }
        }
    }

    private String buildModulesDirectory() {
        StringBuilder result = new StringBuilder();
        result.append(foundModulesDirectory.getAbsolutePath());
        result.append(File.pathSeparator);
        result.append(notModulesDirectory.getAbsolutePath());
        if (providedModuleDirectories.size() > 0) {
            for (File file : providedModuleDirectories) {
                result.append(File.pathSeparator);
                result.append(file.getAbsolutePath());
            }
        }

        return result.toString();
    }

    private String findModInfo(File jar) throws IOException {
        String matchName = jar.getName().replace("-", ".");

        if (moduleInfoWorkDirectory == null)
            throw new IOException("No module info output directory set");
        if (!moduleInfoWorkDirectory.exists())
            Files.createDirectories(moduleInfoWorkDirectory.toPath());
        if (!moduleInfoWorkDirectory.isDirectory())
            throw new IOException("module info output directory is not a directory");

        try {
            File moduleInfoLocation = new File(moduleInfoWorkDirectory.getAbsolutePath() + File.separatorChar + jar.getName().replace(".jar", ""));
            File generatedInfo = moduleInfoLocation.listFiles()[0].listFiles()[0].listFiles()[0].listFiles()[0];
            return Files.readString(Path.of(generatedInfo.getAbsolutePath()), StandardCharsets.US_ASCII);
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            throw new IllegalArgumentException("Unable to find a module info for " + jar);
        }
    }

    private boolean matches(JarFile jarFile, List<String> matcher, String description) {
        if (matcher == null && debug)
            logger.warn("No matcher for " + description + " defined");

        if (matcher != null)
            for (String s : matcher)
                if (jarFile.getName().contains(s))
                    return true;
        return false;
    }
}
