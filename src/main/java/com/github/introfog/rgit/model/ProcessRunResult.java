package com.github.introfog.rgit.model;

public class ProcessRunResult {
    private final String outputLog;

    private final String errorLog;

    private final int exitCode;

    public ProcessRunResult(String outputLog, String errorLog, int exitCode) {
        this.outputLog = outputLog;
        this.errorLog = errorLog;
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOutputLog() {
        return outputLog;
    }

    public String getErrorLog() {
        return errorLog;
    }
}
