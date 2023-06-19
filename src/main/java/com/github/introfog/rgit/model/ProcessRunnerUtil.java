package com.github.introfog.rgit.model;

import java.io.File;
import java.io.IOException;

public final class ProcessRunnerUtil {
    public static ProcessRunResult runProcess(String[] command, File workingDir)
            throws IOException, InterruptedException {
        // Create a ProcessBuilder for the git command
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // Set the repository folder as the working directory
        processBuilder.directory(workingDir);
        // Start the process
        Process process = processBuilder.start();

        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");

        outputGobbler.start();
        errorGobbler.start();

        // Wait for the process to complete
        int exitCode = process.waitFor();

        errorGobbler.join();
        outputGobbler.join();

        return new ProcessRunResult(outputGobbler.getOutput(), errorGobbler.getOutput(), exitCode);
    }
}
