package io.github.wiverson.test;

import io.github.wiverson.ToolProviderAdapterHints;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

import static io.github.wiverson.ToolProviderAdapterHints.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ToolProviderAdapterCoreTest {

    final Logger logger = LoggerFactory.getLogger(ToolProviderAdapterCoreTest.class);

    String[] tools = {"jpackage", "jar", "javac", "jdeps", "jlink"};

    @Test
    public void BasicCheck() throws MojoExecutionException {

        for (String toolName : tools) {
            ToolProviderAdapterHints tool = new ToolProviderAdapterHints();
            tool.setToolName(toolName);
            tool.setFailOnError(false);
            tool.execute();
        }
    }

    @Test
    public void BadArgumentsCheck() {
        ToolProviderAdapterHints tool = new ToolProviderAdapterHints();
        tool.setToolName("jar");
        tool.setFailOnError(true);
        tool.setArgs(new String[]{"barf"});

        boolean exceptionFound = false;

        try {
            tool.execute();
        } catch (MojoExecutionException e) {
            exceptionFound = true;
        }

        assertTrue(exceptionFound);
        assertEquals(1, tool.getErrorCode());
    }

    @Test
    public void ArgumentsCheck() throws MojoExecutionException {

        for (String toolName : tools) {
            ToolProviderAdapterHints tool = new ToolProviderAdapterHints();
            tool.setToolName(toolName);
            tool.setFailOnError(false);
            tool.setArgs(new String[]{"--help"});
            tool.setWriteOutputToLog(false);
            tool.execute();
        }
    }

    @Test
    public void ListUnsupportedTools() {
        String[] notSupported = new String[]{
                "jaotc", "jarsigner", "java", "jcmd", "jconsole", "jdb",
                "jdeprscan", "jfr", "jhsdb", "jimage", "jinfo", "jps", "jmod",
                "jrunscript", "jshell", "jstack", "jstat", "jstatd", "rmid", "rmiregistry",
                "serialver", "jar", "javac", "javadoc", "javap", "jdeps", "jlink", "jpackage", "jmap"
        };

        for (String toolName : notSupported) {
            ToolProviderAdapterHints tool = new ToolProviderAdapterHints();
            tool.setToolName(toolName);
            tool.setFailOnError(false);
            tool.setWriteErrorsToLog(false);
            tool.setWriteOutputToLog(false);
            tool.setArgs(new String[]{"--version"});
            try {
                tool.execute();
                if (tool.failed())
                    logger.info(toolName + " NOT available.");
                else
                    logger.info(toolName + " available.");
            } catch (MojoExecutionException e) {
                logger.error(toolName + " NOT available.");
            }
        }
    }

    @Test
    public void TestHints() throws NoSuchFieldException, IllegalAccessException {
        verify(new ToolProviderAdapterHints(), "classPath", CLASS_PATH);
        verify(new ToolProviderAdapterHints(), "modulePath", MODULE_PATH);
        verify(new ToolProviderAdapterHints(), "addModules", ADD_MODULES);
        verify(new ToolProviderAdapterHints(), "generateModuleInfo", GENERATE_MODULE_INFO);
        verify(new ToolProviderAdapterHints(), "launcher", LAUNCHER);
        verify(new ToolProviderAdapterHints(), "output", OUTPUT);
        verify(new ToolProviderAdapterHints(), "jlinkOptions", JLINK_OPTIONS);
        verify(new ToolProviderAdapterHints(), "name", NAME);
        verify(new ToolProviderAdapterHints(), "icon", ICON);
        verify(new ToolProviderAdapterHints(), "module", MODULE);
        verify(new ToolProviderAdapterHints(), "dest", DEST);
        verify(new ToolProviderAdapterHints(), "type", TYPE);
        verify(new ToolProviderAdapterHints(), "winMenuGroup", WIN_MENU_GROUP);
        verify(new ToolProviderAdapterHints(), "appVersion", APP_VERSION);
        verify(new ToolProviderAdapterHints(), "copyright", COPYRIGHT);
        verify(new ToolProviderAdapterHints(), "description", DESCRIPTION);
        verify(new ToolProviderAdapterHints(), "temp", TEMP);
        verify(new ToolProviderAdapterHints(), "vendor", VENDOR);
        verify(new ToolProviderAdapterHints(), "runtimeImage", RUNTIME_IMAGE);
        verify(new ToolProviderAdapterHints(), "input", INPUT);
        verify(new ToolProviderAdapterHints(), "javaOptions", JAVA_OPTIONS);
        verify(new ToolProviderAdapterHints(), "mainJar", MAIN_JAR);
        verify(new ToolProviderAdapterHints(), "macPackageIdentifier", MAC_PACKAGE_IDENTIFIER);
        verify(new ToolProviderAdapterHints(), "macPackageName", MAC_PACKAGE_NAME);
        verify(new ToolProviderAdapterHints(), "macPackageSigningPrefix", MAC_PACKAGE_SIGNING_PREFIX);
        verify(new ToolProviderAdapterHints(), "macSigningKeychain", MAC_SIGNING_KEYCHAIN);
        verify(new ToolProviderAdapterHints(), "macSigningKeyUserName", MAC_SIGNING_KEY_USER_NAME);
        verify(new ToolProviderAdapterHints(), "appImage", APP_IMAGE);
        verify(new ToolProviderAdapterHints(), "fileAssociations", FILE_ASSOCIATIONS);
        verify(new ToolProviderAdapterHints(), "installDir", INSTALL_DIR);
        verify(new ToolProviderAdapterHints(), "licenseFile", LICENSE_FILE);
        verify(new ToolProviderAdapterHints(), "resourceDir", RESOURCE_DIR);
        verify(new ToolProviderAdapterHints(), "upgradeModulePath", UPGRADE_MODULE_PATH);
        verify(new ToolProviderAdapterHints(), "system", SYSTEM);
        verify(new ToolProviderAdapterHints(), "multiRelease", MULTI_RELEASE);
        verify(new ToolProviderAdapterHints(), "generateOpenModule", GENERATE_OPEN_MODULE);
        verify(new ToolProviderAdapterHints(), "check", CHECK);
        verify(new ToolProviderAdapterHints(), "regex", REGEX);
        verify(new ToolProviderAdapterHints(), "require", REQUIRE);
        verify(new ToolProviderAdapterHints(), "compress", COMPRESS);
        verify(new ToolProviderAdapterHints(), "disablePlugin", DISABLE_PLUGIN);
        verify(new ToolProviderAdapterHints(), "limitModules", LIMIT_MODULES);
        verify(new ToolProviderAdapterHints(), "postProcessPath", POST_PROCESS_PATH);
        verify(new ToolProviderAdapterHints(), "resourcesLastSorter", RESOURCES_LAST_SORTER);
        verify(new ToolProviderAdapterHints(), "saveOpts", SAVE_OPTS);
        verify(new ToolProviderAdapterHints(), "suggestProviders", SUGGEST_PROVIDERS);
        verify(new ToolProviderAdapterHints(), "dir", DIR);
        verify(new ToolProviderAdapterHints(), "exclude", EXCLUDE);
        verify(new ToolProviderAdapterHints(), "hashModules", HASH_MODULES);
        verify(new ToolProviderAdapterHints(), "headerFiles", HEADER_FILES);
    }

    private void verify(ToolProviderAdapterHints toolProviderAdapterHints, String property, String argument) throws NoSuchFieldException, IllegalAccessException {

        assertThat(toolProviderAdapterHints.addShortcutArguments().size()).isEqualTo(0);
        Field field = toolProviderAdapterHints.getClass().getField(property);

        assertThat(field.get(toolProviderAdapterHints)).isEqualTo("");
        String testValue = "testValue";

        field.set(toolProviderAdapterHints, testValue);

        List<String> shortcuts = toolProviderAdapterHints.addShortcutArguments();
        assertThat(shortcuts.size()).isEqualTo(2);
        assertThat(shortcuts.get(0)).startsWith("--");
        assertThat(shortcuts.get(0)).endsWith(argument);

        assertThat(shortcuts.get(1)).isEqualTo(testValue);

    }
}
