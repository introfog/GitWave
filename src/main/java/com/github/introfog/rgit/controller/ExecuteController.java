package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.StagesUtil;
import com.github.introfog.rgit.model.StagesUtil.FxmlStageHolder;
import com.github.introfog.rgit.model.dto.CommandDto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteController.class);

    @FXML
    private TextField directory;

    @FXML
    private AnchorPane anchor;

    @FXML
    private TextField gitCommand;

    @FXML
    private TextField gitComment;

    @FXML
    private Button run;

    @FXML
    private Button edit;

    @FXML
    private Button clean;

    @FXML
    private Button save;

    @FXML
    protected void browseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String lastOpenedFolderPath = AppConfig.getInstance().getLastOpenedFolderInRegistry();
        if (lastOpenedFolderPath != null && !lastOpenedFolderPath.isEmpty()) {
            File lastOpenedFolder = new File(lastOpenedFolderPath);
            if (lastOpenedFolder.exists() && lastOpenedFolder.isDirectory()) {
                directoryChooser.setInitialDirectory(lastOpenedFolder);
                // TODO is it necessary to remove field in config if folder doesn't exist?
            }
        }

        Stage stage = (Stage) anchor.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            directory.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    protected void editCommand() {
        FxmlStageHolder holder = StagesUtil.setUpModalStage("view/editor.fxml", "Command editor");

        EditController editController = holder.getFxmlLoader().getController();
        editController.setExecuteController(this);
        editController.setCommand(new CommandDto(gitCommand.getText(), gitComment.getText()));

        holder.getStage().showAndWait();
    }

    @FXML
    protected void chooseFromSaved() {
        FxmlStageHolder holder = StagesUtil.setUpModalStage("view/explorer.fxml", "Command explorer");

        ExploreController exploreController = holder.getFxmlLoader().getController();
        exploreController.fill();
        exploreController.setMainController(this);

        holder.getStage().showAndWait();
    }

    @FXML
    protected void saveCommand() {
        FxmlStageHolder holder = StagesUtil.setUpModalStage("view/saver.fxml", "Command saver");

        SaveController saveController = holder.getFxmlLoader().getController();
        saveController.setExecuteController(this);

        final String gitCommandText = gitCommand.getText();
        if (!gitCommandText.isEmpty()) {
            saveController.setCommand(gitCommandText);
        }

        final String gitCommentText = gitComment.getText();
        if (!gitCommentText.isEmpty()) {
            saveController.setComment(gitCommentText);
        }

        holder.getStage().showAndWait();
    }

    @FXML
    protected void cleanCommand() {
        gitCommand.clear();
        gitComment.clear();
        switchToSavedCommand(false);
    }

    @FXML
    protected void runGitCommand() {
        if (AppConfig.getInstance().isPathToGitSpecified()) {
            if (directory.getText().isEmpty() || gitCommand.getText().isEmpty()) {
                LOGGER.warn("Either directory '{}' or git command '{}' is empty, running git command was skipped.",
                        directory.getText(), gitCommand.getText());
                // TODO open alert window
                return;
            }

            AppConfig.getInstance().setLastOpenedFolderInRegistry(directory.getText());

            run.setDisable(true);
            new Thread(() -> {
                searchAndExecuteGitCommand(directory.getText(), gitCommand.getText());
                run.setDisable(false);
            }).start();
        } else {
            // TODO probably worth to add a alert or message in setting window if path is specified but file doesn't exist
            StagesUtil.setUpModalStage("view/settings.fxml", "Settings").getStage().showAndWait();
        }
    }

    public void setGitCommand(CommandDto commandDto) {
        gitCommand.setText(commandDto.getCommand());
        gitComment.setText(commandDto.getComment());
        switchToSavedCommand(true);
    }

    private void switchToSavedCommand(boolean switchToSavedCommand) {
        gitCommand.setEditable(!switchToSavedCommand);
        gitCommand.setDisable(switchToSavedCommand);
        gitCommand.setEditable(!switchToSavedCommand);
        gitComment.setDisable(switchToSavedCommand);
        edit.setDisable(!switchToSavedCommand);
        clean.setDisable(!switchToSavedCommand);
        save.setDisable(switchToSavedCommand);
    }

    private void searchAndExecuteGitCommand(String folderPath, String gitCommand) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            List<File> repositoriesToRunCommand = new ArrayList<>();
            searchGitRepositories(folder, repositoriesToRunCommand);
            File scriptFile = null;
            try {
                scriptFile = createAndFillTempFileWithGitCommand(gitCommand, repositoriesToRunCommand);
                executeGitCommand(scriptFile);
            } catch (IOException e) {
                LOGGER.error("Something goes wrong with creation or executing of temp file with git command.", e);
            } finally {
                if (scriptFile != null && scriptFile.exists()) {
                    if (scriptFile.delete()) {
                        LOGGER.info("Temp file '{}' was removed.", scriptFile.getAbsolutePath());
                    } else {
                        LOGGER.warn("Temp file '{}' with git command wasn't removed.", scriptFile.getAbsolutePath());
                    }
                }
            }
        } else {
            LOGGER.warn("Specified folder either don't exist or isn't a directory, running git command was skipped.");
            // TODO open error windows
        }
    }

    private void executeGitCommand(File scriptFile) throws IOException {
        String[] str= { "cmd", "/c", "start", "\"\"",
                "\"" + AppConfig.getInstance().getPathToGitBashExe() + "\"", "-c",
                scriptFile.getAbsolutePath().replace("\\", "\\\\\\\\") + ";read -p 'Press Enter to close window...'" };
        Process powerShellProcess = Runtime.getRuntime().exec(str);
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            LOGGER.info("Standard output of executed git command '{}'", line);
        }
        stdout.close();
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            LOGGER.error("Error output of executed git command '{}'", line);
        }
        stderr.close();
        LOGGER.info("Finish executing git command.");
    }

    private File createAndFillTempFileWithGitCommand(String gitCommand, List<File> repositoriesToRunCommand) throws IOException {
        File scriptFile = File.createTempFile("script", ".sh");
        LOGGER.info("Temp file '{}' was created", scriptFile.getAbsolutePath());
        FileWriter writer = new FileWriter(scriptFile);
        writer.write("#!/bin/bash\n");
        writer.write("echo -e \"\\033[0;32m\" \"" + gitCommand + "\" \"\\033[0m\"\n");
        for (File currentFolder : repositoriesToRunCommand) {
            writer.write("cd \"" + currentFolder.getAbsolutePath().replace("\\", "\\\\") + "\"\n");
            writer.write("echo -e \"\\033[0;36m\" $PWD \"\\033[0m\"\n");

            writer.write(gitCommand + "\n");
        }
        writer.close();
        LOGGER.info("Git command '{}' was written to temp file.", gitCommand);
        return scriptFile;
    }

    private void searchGitRepositories(File folder, List<File> repositoriesToRunCommand) {
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

    private boolean isGitRepository(File folder) {
        File gitFolder = new File(folder, ".git");
        return gitFolder.exists() && gitFolder.isDirectory();
    }
}