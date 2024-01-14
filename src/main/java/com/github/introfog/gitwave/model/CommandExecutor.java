/*
 * Copyright 2023-2024 Dmitry Chubrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.introfog.gitwave.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommandExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);

    private CommandExecutor() {
        // Empty constructor
    }

    public static File searchGitRepositoriesAndCreateScriptFile(File directory, String command) {
        LOGGER.info("Start searching git repositories in '{}' path.", directory.getAbsolutePath());
        List<File> repositoriesToRunCommand = new ArrayList<>();
        searchGitRepositories(directory, repositoriesToRunCommand);
        LOGGER.info("'{}' git repositories were found to run command.", repositoriesToRunCommand.size());
        try {
            return createAndFillScriptFileWithCommand(command, repositoriesToRunCommand);
        } catch (IOException e) {
            LOGGER.error("Something goes wrong with creation temp script file with command.", e);
        }

        return null;
    }

    public static void executeScriptFileWithCommand(File scriptFile) {
        try {
            LOGGER.info("Start executing script file '{}'.", scriptFile.getAbsolutePath());
            executeScriptFile(scriptFile);
            LOGGER.info("Finish executing git command.");
        } catch (IOException e) {
            LOGGER.error("Something goes wrong with executing temp script file with command.", e);
        } finally {
            if (scriptFile != null && scriptFile.exists()) {
                if (scriptFile.delete()) {
                    LOGGER.info("Temp script file '{}' was removed.", scriptFile.getAbsolutePath());
                } else {
                    LOGGER.warn("Temp script file '{}' with command wasn't removed.", scriptFile.getAbsolutePath());
                }
            }
        }
    }

    private static void executeScriptFile(File scriptFile) throws IOException {
        Process powerShellProcess = Runtime.getRuntime().exec(constructCmdCommand(scriptFile));
        String line;

        BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            LOGGER.info("Standard output of executed command '{}'", line);
        }
        stdout.close();

        BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            LOGGER.error("Error output of executed command '{}'", line);
        }
        stderr.close();
    }

    private static String[] constructCmdCommand(File scriptFile) {
        List<String> cmdCommand = new ArrayList<>();
        cmdCommand.add("cmd");
        cmdCommand.add("/c");
        cmdCommand.add("start");
        cmdCommand.add("\"\"");
        cmdCommand.add("\"" + AppConfig.getInstance().getPathToGitBashExe() + "\"");
        cmdCommand.add("-c");
        cmdCommand.add(scriptFile.getAbsolutePath().replace("\\", "\\\\\\\\") + ";read -p 'Press Enter to close window...'");
        return cmdCommand.toArray(new String[]{});
    }

    private static File createAndFillScriptFileWithCommand(String gitCommand, List<File> repositoriesToRunCommand) throws IOException {
        File tempDir = new File("temp");
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            LOGGER.error("Failed to create the temp directory '{}'.", "temp");
        }

        File scriptFile = File.createTempFile("script", ".sh", tempDir);
        LOGGER.info("Temp script file '{}' was created", scriptFile.getAbsolutePath());

        FileWriter writer = new FileWriter(scriptFile);
        writer.write("#!/bin/bash\n");
        writer.write("echo -e \"\\033[0;32m\" \"" + gitCommand + "\" \"\\033[0m\"\n");
        for (File currentFolder : repositoriesToRunCommand) {
            writer.write("cd \"" + currentFolder.getAbsolutePath().replace("\\", "\\\\") + "\"\n");
            writer.write("echo -e \"\\033[0;36m\" $PWD \"\\033[0m\"\n");

            writer.write(gitCommand + "\n");
        }
        writer.close();
        LOGGER.info("Command '{}' was written to temp script file.", gitCommand);
        return scriptFile;
    }

    private static void searchGitRepositories(File folder, List<File> repositoriesToRunCommand) {
        if (isGitRepository(folder)) {
            repositoriesToRunCommand.add(folder);
        } else {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (isGitRepository(file)) {
                            repositoriesToRunCommand.add(file);
                        } else {
                            searchGitRepositories(file, repositoriesToRunCommand);
                        }
                    }
                }
            }
        }
    }

    private static boolean isGitRepository(File folder) {
        File gitFolder = new File(folder, ".git");
        return gitFolder.exists() && gitFolder.isDirectory();
    }
}
