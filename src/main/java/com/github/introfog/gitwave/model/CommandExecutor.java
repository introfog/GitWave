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

import com.github.introfog.gitwave.controller.main.ExecutionController;

import java.io.BufferedReader;
import java.io.File;
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

    public static List<File> searchGitRepositories(File directory) {
        LOGGER.info("Start searching git repositories in '{}' path.", directory.getAbsolutePath());
        List<File> repositoriesToRunCommand = new ArrayList<>();
        searchGitRepositories(directory, repositoriesToRunCommand);
        LOGGER.info("'{}' git repositories were found to run command.", repositoriesToRunCommand.size());
        return repositoriesToRunCommand;
    }

    public static void executeCommand(List<File> repositoriesToRunCommand, ExecutionController controller, String command) {
        LOGGER.info("Start executing command '{}'.", command);
        try {
            final List<RunnableCommand> runnableCommands = createRunnableCommands(repositoriesToRunCommand, command);
            controller.writeCommand(command);
            for (RunnableCommand runnableCommand : runnableCommands) {
                if (AppConfig.getInstance().appWasClosed() || controller.wasClosed()) {
                    return;
                }
                controller.writeDirectory(runnableCommand.path);
                Process process = Runtime.getRuntime().exec(runnableCommand.params);

                String line;
                BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((line = stdout.readLine()) != null) {
                    controller.writeStandardOutput(line);
                }
                stdout.close();

                BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = stderr.readLine()) != null) {
                    controller.writeErrorOutput(line);
                }
                stderr.close();
            }
            controller.writeFinishMessage();
            LOGGER.info("Finish executing git command '{}'.", command);
        } catch (IOException e) {
            LOGGER.error("Something goes wrong with executing command.", e);
        }
    }

    private static List<RunnableCommand> createRunnableCommands(List<File> repositoriesToRunCommand, String command) {
        List<RunnableCommand> runnableCommands = new ArrayList<>();
        for (File gitRepo : repositoriesToRunCommand) {
            String gitRepoPath = gitRepo.getAbsolutePath().replace("\\", "/");
            runnableCommands.add(new RunnableCommand(constructCmdCommand(gitRepoPath, command), gitRepoPath));
        }
        return runnableCommands;
    }

    private static String[] constructCmdCommand(String gitRepoPath, String command) {
        List<String> cmdCommand = new ArrayList<>();
        cmdCommand.add(AppConfig.getInstance().getPathToBash());
        cmdCommand.add("-c");
        cmdCommand.add("cd '" + gitRepoPath + "' && " + command);
        return cmdCommand.toArray(new String[]{});
    }

    private static void searchGitRepositories(File folder, List<File> repositoriesToRunCommand) {
        if (AppConfig.getInstance().appWasClosed()) {
            return;
        }
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

    private static class RunnableCommand {
        private final String[] params;
        private final String path;


        private RunnableCommand(String[] params, String path) {
            this.params = params;
            this.path = path;
        }
    }
}
