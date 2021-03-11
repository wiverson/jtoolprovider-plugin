package io.github.wiverson;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Mojo(name = "java-tool")
public class ToolProviderAdapterHints extends ToolProviderAdapterCore {

    static public final String CLASS_PATH = "class-path";
    static public final String MODULE_PATH = "module-path";
    static public final String ADD_MODULES = "add-modules";
    static public final String GENERATE_MODULE_INFO = "generate-module-info";
    static public final String LAUNCHER = "launcher";
    static public final String OUTPUT = "output";
    static public final String JLINK_OPTIONS = "jlink-options";
    static public final String NAME = "name";
    static public final String ICON = "icon";
    static public final String MODULE = "module";
    static public final String DEST = "dest";
    static public final String TYPE = "type";
    static public final String WIN_MENU_GROUP = "win-menu-group";
    static public final String APP_VERSION = "app-version";
    static public final String COPYRIGHT = "copyright";
    static public final String DESCRIPTION = "description";
    static public final String TEMP = "temp";
    static public final String VENDOR = "vendor";
    static public final String RUNTIME_IMAGE = "runtime-image";
    static public final String INPUT = "input";
    static public final String JAVA_OPTIONS = "java-options";
    static public final String MAIN_JAR = "main-jar";
    static public final String MAC_PACKAGE_IDENTIFIER = "mac-package-identifier";
    static public final String MAC_PACKAGE_NAME = "mac-package-name";
    static public final String MAC_PACKAGE_SIGNING_PREFIX = "mac-package-signing-prefix";
    static public final String MAC_SIGNING_KEYCHAIN = "mac-signing-keychain";
    static public final String MAC_SIGNING_KEY_USER_NAME = "mac-signing-key-user-name";
    static public final String APP_IMAGE = "app-image";
    static public final String FILE_ASSOCIATIONS = "file-associations";
    static public final String INSTALL_DIR = "install-dir";
    static public final String LICENSE_FILE = "license-file";
    static public final String RESOURCE_DIR = "resource-dir";
    /**
     * jdeps specific
     */
    static public final String UPGRADE_MODULE_PATH = "upgrade-module-path";
    static public final String SYSTEM = "system";
    static public final String MULTI_RELEASE = "multi-release";
    static public final String GENERATE_OPEN_MODULE = "generate-open-module";
    static public final String CHECK = "check";
    static public final String REGEX = "regex";
    static public final String REQUIRE = "require";
    /**
     * jlink specific
     */
    static public final String COMPRESS = "compress";
    static public final String DISABLE_PLUGIN = "disable-plugin";
    static public final String LIMIT_MODULES = "limit-modules";
    static public final String POST_PROCESS_PATH = "post-process-path";
    static public final String RESOURCES_LAST_SORTER = "resources-last-sorter";
    static public final String SAVE_OPTS = "save-opts";
    static public final String SUGGEST_PROVIDERS = "suggest-providers";
    /**
     * jmod specifi
     */
    static public final String DIR = "dir";
    static public final String EXCLUDE = "exclude";
    static public final String HASH_MODULES = "hash-modules";
    static public final String HEADER_FILES = "header-files";
    /**
     * jpackage options
     */
    @Parameter(property = CLASS_PATH)
    public String classPath = "";
    @Parameter(property = MODULE_PATH)
    public String modulePath = "";
    @Parameter(property = ADD_MODULES)
    public String addModules = "";
    @Parameter(property = GENERATE_MODULE_INFO)
    public String generateModuleInfo = "";
    @Parameter(property = LAUNCHER)
    public String launcher = "";
    @Parameter(property = OUTPUT)
    public String output = "";
    @Parameter(property = JLINK_OPTIONS)
    public String jlinkOptions = "";
    @Parameter(property = NAME)
    public String name = "";
    @Parameter(property = ICON)
    public String icon = "";
    @Parameter(property = MODULE)
    public String module = "";
    @Parameter(property = DEST)
    public String dest = "";
    @Parameter(property = TYPE)
    public String type = "";
    @Parameter(property = WIN_MENU_GROUP)
    public String winMenuGroup = "";
    @Parameter(property = APP_VERSION)
    public String appVersion = "";
    @Parameter(property = COPYRIGHT)
    public String copyright = "";
    @Parameter(property = DESCRIPTION)
    public String description = "";
    @Parameter(property = TEMP)
    public String temp = "";
    @Parameter(property = VENDOR)
    public String vendor = "";
    @Parameter(property = RUNTIME_IMAGE)
    public String runtimeImage = "";
    @Parameter(property = INPUT)
    public String input = "";
    @Parameter(property = JAVA_OPTIONS)
    public String javaOptions = "";
    @Parameter(property = MAIN_JAR)
    public String mainJar = "";
    @Parameter(property = MAC_PACKAGE_IDENTIFIER)
    public String macPackageIdentifier = "";
    @Parameter(property = MAC_PACKAGE_NAME)
    public String macPackageName = "";
    @Parameter(property = MAC_PACKAGE_SIGNING_PREFIX)
    public String macPackageSigningPrefix = "";
    @Parameter(property = MAC_SIGNING_KEYCHAIN)
    public String macSigningKeychain = "";
    @Parameter(property = MAC_SIGNING_KEY_USER_NAME)
    public String macSigningKeyUserName = "";
    @Parameter(property = APP_IMAGE)
    public String appImage = "";
    @Parameter(property = FILE_ASSOCIATIONS)
    public String fileAssociations = "";
    @Parameter(property = INSTALL_DIR)
    public String installDir = "";
    @Parameter(property = LICENSE_FILE)
    public String licenseFile = "";
    @Parameter(property = RESOURCE_DIR)
    public String resourceDir = "";
    @Parameter(property = UPGRADE_MODULE_PATH)
    public String upgradeModulePath = "";
    @Parameter(property = SYSTEM)
    public String system = "";
    @Parameter(property = MULTI_RELEASE)
    public String multiRelease = "";
    @Parameter(property = GENERATE_OPEN_MODULE)
    public String generateOpenModule = "";
    @Parameter(property = CHECK)
    public String check = "";
    @Parameter(property = REGEX)
    public String regex = "";
    @Parameter(property = REQUIRE)
    public String require = "";
    @Parameter(property = COMPRESS)
    public String compress = "";
    @Parameter(property = DISABLE_PLUGIN)
    public String disablePlugin = "";
    @Parameter(property = LIMIT_MODULES)
    public String limitModules = "";
    @Parameter(property = POST_PROCESS_PATH)
    public String postProcessPath = "";
    @Parameter(property = RESOURCES_LAST_SORTER)
    public String resourcesLastSorter = "";
    @Parameter(property = SAVE_OPTS)
    public String saveOpts = "";
    @Parameter(property = SUGGEST_PROVIDERS)
    public String suggestProviders = "";
    @Parameter(property = DIR)
    public String dir = "";
    @Parameter(property = EXCLUDE)
    public String exclude = "";
    @Parameter(property = HASH_MODULES)
    public String hashModules = "";
    @Parameter(property = HEADER_FILES)
    public String headerFiles = "";
    @Parameter
    public List<File> cleanDirectories;

    @Parameter
    public List<File> removeDirectories;

    @Parameter(defaultValue = "false")
    private boolean skip = false;

    private boolean failed;

    private void add(List<String> list, String arg, String value) {
        if (value == null)
            return;
        if (value.isEmpty())
            return;
        list.add("--" + arg);
        list.add(value);

    }

    public List<String> addShortcutArguments() {
        List<String> result = new ArrayList<>();

        add(result, CLASS_PATH, classPath);
        add(result, ADD_MODULES, addModules);
        add(result, APP_IMAGE, appImage);
        add(result, APP_VERSION, appVersion);
        add(result, CHECK, check);
        add(result, COMPRESS, compress);
        add(result, COPYRIGHT, copyright);
        add(result, DESCRIPTION, description);
        add(result, DEST, dest);
        add(result, DIR, dir);
        add(result, DISABLE_PLUGIN, disablePlugin);
        add(result, EXCLUDE, exclude);
        add(result, FILE_ASSOCIATIONS, fileAssociations);
        add(result, GENERATE_MODULE_INFO, generateModuleInfo);
        add(result, GENERATE_OPEN_MODULE, generateOpenModule);
        add(result, HASH_MODULES, hashModules);
        add(result, HEADER_FILES, headerFiles);
        add(result, ICON, icon);
        add(result, INPUT, input);
        add(result, INSTALL_DIR, installDir);
        add(result, JAVA_OPTIONS, javaOptions);
        add(result, JLINK_OPTIONS, jlinkOptions);
        add(result, LAUNCHER, launcher);
        add(result, LICENSE_FILE, licenseFile);
        add(result, LIMIT_MODULES, limitModules);
        add(result, MAC_PACKAGE_IDENTIFIER, macPackageIdentifier);
        add(result, MAC_PACKAGE_NAME, macPackageName);
        add(result, MAC_PACKAGE_SIGNING_PREFIX, macPackageSigningPrefix);
        add(result, MAC_SIGNING_KEY_USER_NAME, macSigningKeyUserName);
        add(result, MAC_SIGNING_KEYCHAIN, macSigningKeychain);
        add(result, MAIN_JAR, mainJar);
        add(result, MODULE_PATH, modulePath);
        add(result, MODULE, module);
        add(result, MULTI_RELEASE, multiRelease);
        add(result, NAME, name);
        add(result, OUTPUT, output);
        add(result, POST_PROCESS_PATH, postProcessPath);
        add(result, REGEX, regex);
        add(result, REQUIRE, require);
        add(result, RESOURCE_DIR, resourceDir);
        add(result, RESOURCES_LAST_SORTER, resourcesLastSorter);
        add(result, RUNTIME_IMAGE, runtimeImage);
        add(result, SAVE_OPTS, saveOpts);
        add(result, SUGGEST_PROVIDERS, suggestProviders);
        add(result, SYSTEM, system);
        add(result, TEMP, temp);
        add(result, TYPE, type);
        add(result, UPGRADE_MODULE_PATH, upgradeModulePath);
        add(result, VENDOR, vendor);
        add(result, WIN_MENU_GROUP, winMenuGroup);

        return result;
    }

    public boolean failed() {
        return failed;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip)
            return;

        clean(cleanDirectories, true);
        clean(removeDirectories, false);

        RunTool runTool = new RunTool(getLog(), debug, writeOutputToLog, writeErrorsToLog);

        List<String> arguments = addShortcutArguments();
        arguments.addAll(Arrays.asList(args));

        runTool.runTool(toolName, arguments, failOnError);
        errorCode = runTool.errorCode;
        failed = runTool.failed;
    }

    private void clean(List<File> directory, boolean replace) throws MojoFailureException {

        if (directory == null)
            return;
        if (directory.size() == 0)
            return;

        for (File clean : directory) {
            try {
                if (clean.exists()) {
                    Files.walk(clean.toPath())
                            .map(Path::toFile)
                            .sorted((o1, o2) -> -o1.compareTo(o2))
                            .forEach(File::delete);
                    if (replace)
                        clean.mkdirs();
                    if (debug)
                        if (replace)
                            getLog().info("Cleaned directory " + clean.getAbsolutePath());
                        else
                            getLog().info("Reset directory " + clean.getAbsolutePath());
                }
            } catch (IOException ioException) {
                getLog().error(ioException);
                throw new MojoFailureException(ioException.getMessage());
            }
        }
    }
}
