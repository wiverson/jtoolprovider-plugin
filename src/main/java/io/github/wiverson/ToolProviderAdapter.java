package io.github.wiverson;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.spi.ToolProvider;

/**
 * Runs arbitrary Java tools as provided by the ToolProvider API.
 */
@Mojo(name = "java-tool")
public class ToolProviderAdapter extends AbstractMojo {

    public enum LogLevel {
        WARN, INFO, ERROR, DEBUG
    }

    private boolean failed = false;

    @Parameter(property = "echoArguments", defaultValue = "false")
    private boolean echoArguments = false;

    @Parameter(property = "writeOutputToLog", defaultValue = "true")
    private boolean writeOutputToLog = true;

    public void setWriteOutputToLog(boolean writeOutputToLog) {
        this.writeOutputToLog = writeOutputToLog;
    }

    public void setWriteErrorsToLog(boolean writeErrorsToLog) {
        this.writeErrorsToLog = writeErrorsToLog;
    }

    @Parameter(property = "writeErrorsToLog", defaultValue = "true")
    private boolean writeErrorsToLog = true;

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

    public int getErrorCode() {
        return errorCode;
    }

    private int errorCode = -1;

    private String normalOutput;
    private String errorOutput;

    private void log(String entry, LogLevel level) {
        if (log != null) {
            switch (level) {
                case WARN:
                    log.warn(entry);
                    break;
                case INFO:
                    log.info(entry);
                    break;
                case ERROR:
                    log.error(entry);
                    break;
                case DEBUG:
                    log.debug(entry);
                    break;
            }
        } else {
            switch (level) {
                case WARN:
                case INFO:
                    if (writeOutputToLog)
                        System.out.println(entry);
                    break;
                case ERROR:
                    if (writeErrorsToLog)
                        System.err.println("ERROR>" + entry);
                    break;
                case DEBUG:
                    if (writeOutputToLog)
                        System.out.println("DEBUG>" + entry);
                    break;
            }
        }
    }

    private ToolProvider result(String toolname) {
        Optional<ToolProvider> result = ToolProvider.findFirst(toolname);

        if (result.isPresent()) {
            var t = result.get();
            return result.get();
        } else {
            log("No " + toolname + " found", LogLevel.ERROR);
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
        if (tool == null)
            if (failOnError)
                throw new MojoExecutionException("Unable to find ToolProvider [" + toolName + "]");
            else {
                log("Unable to find ToolProvider [" + toolName + "]", LogLevel.ERROR);
                return;
            }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        errorCode = -1;

        final String utf8 = StandardCharsets.UTF_8.name();
        try (PrintStream output = new PrintStream(outputStream, true, utf8)) {
            try (PrintStream error = new PrintStream(outputStream, true, utf8)) {
                errorCode = tool.run(output, error, args);
            } finally {
                errorStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (errorCode != 0) {
            log(toolName + " failed with error code [" + errorCode + "]", LogLevel.ERROR);
            for (String arg : args) {
                log("   " + arg, LogLevel.ERROR);
            }

        } else {
            if (echoArguments)
                for (String arg : args) {
                    log("   " + arg, LogLevel.INFO);
                }
        }

        try {
            String output = outputStream.toString(utf8);
            String error = errorStream.toString(utf8);
            if (output.endsWith("\n"))
                output = output.substring(0, output.length() - 1);
            if (error.endsWith("\n"))
                error = output.substring(0, output.length() - 1);
            if (output.length() > 0)
                normalOutput = output;
            if (error.length() > 0)
                errorOutput = error;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (writeOutputToLog || errorCode != 0)
            if (normalOutput != null && normalOutput.length() > 0)
                log(normalOutput, LogLevel.INFO);
        if (writeErrorsToLog || errorCode != 0)
            if (errorOutput != null && errorOutput.length() > 0)
                log(errorOutput, LogLevel.ERROR);

        if (failOnError && errorCode != 0)
            throw new MojoExecutionException(toolName + " " + errorCode);
    }
}
