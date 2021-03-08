package io.github.wiverson;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.spi.ToolProvider;

public class RunTool {

    private final Log log;
    private boolean writeOutputToLog;
    private boolean writeErrorsToLog;

    public boolean failed;
    public int errorCode;
    public boolean echoArguments = false;

    public RunTool(Log log, boolean echoArguments, boolean writeOutputToLog, boolean writeErrorsToLog) {
        this.log = log;
        this.writeOutputToLog = writeOutputToLog;
        this.writeErrorsToLog = writeErrorsToLog;
        this.echoArguments = echoArguments;
    }

    public RunTool(Log log, boolean echoArguments) {
        this.log = log;
        this.writeErrorsToLog = true;
        this.writeOutputToLog = true;
        this.echoArguments = echoArguments;
    }

    protected void log(String entry, ToolProviderAdapterCore.LogLevel level) {
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


    protected ToolProvider result(String toolname) {
        Optional<ToolProvider> result = ToolProvider.findFirst(toolname);

        if (result.isPresent()) {
            var t = result.get();
            return result.get();
        } else {
            log("No " + toolname + " found", ToolProviderAdapterCore.LogLevel.ERROR);
            failed = true;
        }

        return null;
    }

    public void runTool(String toolName, List<String> args, boolean failOnError) throws MojoExecutionException {

        errorCode = -1;

        ToolProvider tool = result(toolName);
        if (tool == null)
            if (failOnError)
                throw new MojoExecutionException("Unable to find ToolProvider [" + toolName + "]");
            else {
                log("Unable to find ToolProvider [" + toolName + "]", ToolProviderAdapterCore.LogLevel.ERROR);
                return;
            }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();


        final String utf8 = StandardCharsets.UTF_8.name();
        try (PrintStream output = new PrintStream(outputStream, true, utf8)) {
            try (PrintStream error = new PrintStream(outputStream, true, utf8)) {
                errorCode = tool.run(output, error, args.toArray(new String[0]));
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
            log(toolName + " failed with error code [" + errorCode + "]", ToolProviderAdapterCore.LogLevel.ERROR);
            for (String arg : args) {
                log("   " + arg, ToolProviderAdapterCore.LogLevel.ERROR);
            }

        } else {
            if (echoArguments)
                for (String arg : args) {
                    log("   " + arg, ToolProviderAdapterCore.LogLevel.INFO);
                }
        }

        String normalOutput = null;
        String errorOutput = null;

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
                log(normalOutput, ToolProviderAdapterCore.LogLevel.INFO);
        if (writeErrorsToLog || errorCode != 0)
            if (errorOutput != null && errorOutput.length() > 0)
                log(errorOutput, ToolProviderAdapterCore.LogLevel.ERROR);

        if (failOnError && errorCode != 0)
            throw new MojoExecutionException(toolName + " " + errorCode);
    }
}

