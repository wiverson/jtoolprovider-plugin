package com.doublerobot.test;

import com.doublerobot.Tool;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToolTest {

    final Logger logger = LoggerFactory.getLogger(ToolTest.class);
    String[] tools = {"jpackage", "jar", "javac", "jdeps", "jlink"};

    @Test
    public void BasicCheck() throws MojoExecutionException {

        for (String toolName : tools) {
            Tool tool = new Tool();
            tool.setToolName(toolName);
            tool.setFailOnError(false);
            tool.execute();
            logger.info(toolName + " found.");

        }
    }

}
