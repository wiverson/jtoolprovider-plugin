package com.doublerobot;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.spi.ToolProvider;

/**
 * Goal which touches a timestamp file.
 *
 * @goal run
 */
@Mojo(name = "java-tool")
public class MavenJavaToolProviderPlugin extends AbstractMojo {

    private boolean failed = false;

    private Log log;

    /**
     * Name of the tool to run
     */
    @Parameter(property = "toolName", required = true)
    private String toolName;

    /**
     * Arguments to pass to tool when run
     */
    @Parameter(property = "args")
    private String[] args = {"--version"};

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    @Parameter(property = "failOnError", defaultValue = "true")
    private boolean failOnError = true;

    @Override
    public void setLog(Log log) {
        this.log = log;
    }

    @Override
    public Log getLog() {
        return log;
    }

    private void log(String entry) {
        if (log != null)
            log.info(entry);
        else System.out.println(">" + entry);
    }

    private ToolProvider result(String toolname) {
        Optional<ToolProvider> result = ToolProvider.findFirst(toolname);

        if (result.isPresent()) {
            var t = result.get();
            return result.get();
        } else {
            log("No " + toolname + " found");
            failed = true;
        }

        return null;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public boolean failed() {
        return failed;
    }

    public void execute() throws MojoExecutionException {

        ToolProvider tool = result(toolName);
        if (tool == null && failOnError)
            throw new MojoExecutionException("Unable to find ToolProvider [" + toolName + "]");

        if (tool == null) {
            log("Unable to find ToolProvider [" + toolName + "]");
            return;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        final String utf8 = StandardCharsets.UTF_8.name();
        try (PrintStream output = new PrintStream(outputStream, true, utf8)) {
            try (PrintStream error = new PrintStream(outputStream, true, utf8)) {
                tool.run(output, error, args);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            String output = outputStream.toString(utf8);
            String error = errorStream.toString(utf8);
            if (output.endsWith("\n"))
                output = output.substring(0, output.length() - 1);
            if (error.endsWith("\n"))
                error = output.substring(0, output.length() - 1);
            if (output.length() > 0)
                log(output);
            if (error.length() > 0)
                log(error);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
