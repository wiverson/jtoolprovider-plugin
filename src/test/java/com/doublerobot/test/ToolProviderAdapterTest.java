package com.doublerobot.test;

import com.doublerobot.ToolProviderAdapter;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ToolProviderAdapterTest {

    final Logger logger = LoggerFactory.getLogger(ToolProviderAdapterTest.class);
    String[] tools = {"jpackage", "jar", "javac", "jdeps", "jlink"};

    @Test
    public void BasicCheck() throws MojoExecutionException {

        for (String toolName : tools) {
            ToolProviderAdapter tool = new ToolProviderAdapter();
            tool.setToolName(toolName);
            tool.setFailOnError(false);
            tool.execute();
            logger.info(toolName + " found.");
        }
    }

    @Test
    public void ArgumentsCheck() throws MojoExecutionException {

        for (String toolName : tools) {
            ToolProviderAdapter tool = new ToolProviderAdapter();
            tool.setToolName(toolName);
            tool.setFailOnError(false);
            tool.setArgs(new String[]{"--help"});
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
            ToolProviderAdapter tool = new ToolProviderAdapter();
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
}
