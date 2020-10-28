package com.doublerobot.test;

import com.doublerobot.MavenJavaToolProviderPlugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.*;


public class MavenJavaToolProviderPluginTest {

    final Logger logger = LoggerFactory.getLogger(MavenJavaToolProviderPluginTest.class);
    String[] tools = {"jpackage", "jar", "javac", "jdeps", "jlink"};

    @Test
    public void BasicCheck() throws MojoExecutionException {

        for (String toolName : tools) {
            MavenJavaToolProviderPlugin mavenJavaToolProviderPlugin = new MavenJavaToolProviderPlugin();
            mavenJavaToolProviderPlugin.setToolName(toolName);
            mavenJavaToolProviderPlugin.setFailOnError(false);
            mavenJavaToolProviderPlugin.execute();
            logger.info(toolName + " found.");
        }
    }

    @Test
    public void ArgumentsCheck() throws MojoExecutionException {

        for (String toolName : tools) {
            MavenJavaToolProviderPlugin mavenJavaToolProviderPlugin = new MavenJavaToolProviderPlugin();
            mavenJavaToolProviderPlugin.setToolName(toolName);
            mavenJavaToolProviderPlugin.setFailOnError(false);
            mavenJavaToolProviderPlugin.setArgs(new String[]{"--help"});
            mavenJavaToolProviderPlugin.execute();
        }
    }

    @Test
    public void ListUnsupportedTools() {
        String[] notSupported = new String[]{
                "jaotc", "jarsigner", "java", "jcmd", "jconsole", "jdb",
                "jdeprscan", "jfr", "jhsdb", "jimage", "jinfo", "jmap", "jps",
                "jrunscript", "jshell", "jstack", "jstat", "jstatd", "rmid", "rmiregistry", "serialver"};

        for (String toolName : notSupported) {
            MavenJavaToolProviderPlugin mavenJavaToolProviderPlugin = new MavenJavaToolProviderPlugin();
            mavenJavaToolProviderPlugin.setToolName(toolName);
            mavenJavaToolProviderPlugin.setFailOnError(false);
            mavenJavaToolProviderPlugin.setArgs(new String[]{"--version"});
            try {
                mavenJavaToolProviderPlugin.execute();
                assertThat(mavenJavaToolProviderPlugin.failed());
                logger.info(toolName + " failed as expected.");
            } catch (MojoExecutionException e) {
                logger.error(toolName + " does NOT work.");
            }
        }

    }

    @Test
    public void ListSupportedTools() {

        String[] works = new String[]{
                "jar", "javac", "javadoc", "javap", "jdeps", "jlink", "jmod", "jpackage"
        };

        for (String toolName : works) {
            MavenJavaToolProviderPlugin mavenJavaToolProviderPlugin = new MavenJavaToolProviderPlugin();
            mavenJavaToolProviderPlugin.setToolName(toolName);
            mavenJavaToolProviderPlugin.setFailOnError(false);
            mavenJavaToolProviderPlugin.setArgs(new String[]{"--version"});
            try {
                mavenJavaToolProviderPlugin.execute();
                assertThat(mavenJavaToolProviderPlugin.failed()).isFalse();
                logger.info(toolName + " worked as expected.");
            } catch (MojoExecutionException e) {
                logger.error(toolName + " does NOT work.");
            }
        }

    }

}
