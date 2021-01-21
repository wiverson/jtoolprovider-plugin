package io.github.wiverson;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Optional;
import java.util.spi.ToolProvider;

/**
 * Runs arbitrary Java tools as provided by the ToolProvider API.
 */
public abstract class ToolProviderAdapterCore extends AbstractMojo {

    private boolean failed = false;
    @Parameter(property = "echoArguments", defaultValue = "false")
    protected boolean echoArguments = false;
    @Parameter(property = "writeOutputToLog", defaultValue = "true")
    protected boolean writeOutputToLog = true;
    @Parameter(property = "writeErrorsToLog", defaultValue = "true")
    protected boolean writeErrorsToLog = true;
    private Log log;
    /**
     * Name of the tool to run
     */
    @Parameter(property = "toolName", required = true)
    protected String toolName;
    /**
     * Arguments to pass to tool when run
     */
    @Parameter(property = "args")
    public String[] args = {"--version"};
    @Parameter(property = "failOnError", defaultValue = "true")
    protected boolean failOnError = true;
    protected int errorCode = -1;
    protected String normalOutput;
    protected String errorOutput;

    public void setWriteOutputToLog(boolean writeOutputToLog) {
        this.writeOutputToLog = writeOutputToLog;
    }

    public void setWriteErrorsToLog(boolean writeErrorsToLog) {
        this.writeErrorsToLog = writeErrorsToLog;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public void setLog(Log log) {
        this.log = log;
    }

    public int getErrorCode() {
        return errorCode;
    }

    protected void log(String entry, LogLevel level) {
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

    public enum LogLevel {
        WARN, INFO, ERROR, DEBUG
    }


}
