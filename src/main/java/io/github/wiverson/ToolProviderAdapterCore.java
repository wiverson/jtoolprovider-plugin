package io.github.wiverson;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Runs arbitrary Java tools as provided by the ToolProvider API.
 */
public abstract class ToolProviderAdapterCore extends AbstractMojo {

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

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public enum LogLevel {
        WARN, INFO, ERROR, DEBUG
    }


}
