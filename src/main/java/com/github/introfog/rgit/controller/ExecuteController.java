package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.DialogFactory;
import com.github.introfog.rgit.model.StageFactory;
import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;
import com.github.introfog.rgit.model.dto.CommandDto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteController extends BaseController {
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
    private Button update;

    @FXML
    private Button clean;

    @FXML
    private Button save;

    private CommandDto savedCommand;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        final Stage primaryStage = fxmlStageHolder.getStage();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            DialogFactory.createCloseConfirmationAlert(primaryStage);
        });
    }

    @FXML
    protected void browseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String lastOpenedFolderPath = AppConfig.getInstance().getLastRunFolder();
        if (lastOpenedFolderPath != null && !lastOpenedFolderPath.isEmpty()) {
            File lastOpenedFolder = new File(lastOpenedFolderPath);
            if (lastOpenedFolder.exists() && lastOpenedFolder.isDirectory()) {
                directoryChooser.setInitialDirectory(lastOpenedFolder);
            }
        }

        Stage stage = (Stage) anchor.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            directory.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    protected void updateCommand() {
        FxmlStageHolder holder = StageFactory.createModalStage("view/updater.fxml", "Command updater");

        UpdateController updateController = holder.getFxmlLoader().getController();
        updateController.setExecuteController(this);
        updateController.setCommand(savedCommand);

        holder.getStage().showAndWait();
    }

    @FXML
    protected void chooseFromSaved() {
        FxmlStageHolder holder = StageFactory.createModalStage("view/explorer.fxml", "Command explorer");

        ExploreController exploreController = holder.getFxmlLoader().getController();
        exploreController.setExecuteController(this);

        holder.getStage().showAndWait();
    }

    @FXML
    protected void saveCommand() {
        if (gitCommand.getText().isEmpty()) {
            DialogFactory.createErrorAlert("Invalid command", "Command can't be empty");
        } else {
            final CommandDto commandDto = new CommandDto(gitCommand.getText(), gitComment.getText());
            AppConfig.getInstance().addCommand(commandDto);
            setGitCommand(commandDto);
        }
    }

    @FXML
    protected void cleanCommand() {
        gitCommand.clear();
        gitComment.clear();
        savedCommand = null;
        switchToSavedCommand(false);
    }

    @FXML
    protected void runGitCommand() {
        final String pathToGitBashExe = AppConfig.getInstance().getPathToGitBashExe();
        if (pathToGitBashExe == null || pathToGitBashExe.isEmpty()) {
            StageFactory.createModalStage("view/settings.fxml", "Settings").getStage().showAndWait();
        } else if (!(new File(pathToGitBashExe)).exists()) {
            LOGGER.error("Specified GitBash.exe path '{}' points to not-existent file, running git command was skipped.", pathToGitBashExe);
            DialogFactory.createErrorAlert("Invalid path to GitBash.exe", "Specified path \"" + pathToGitBashExe +
                    "\" points to not-existent file. Specify correct path in settings.");
        } else {
            if (gitCommand.getText().isEmpty()) {
                LOGGER.warn("Command '{}' is empty, running git command was skipped.", gitCommand.getText());
                DialogFactory.createErrorAlert("Invalid command", "Command can't be empty");
                return;
            }
            if (directory.getText().isEmpty()) {
                LOGGER.warn("Directory '{}' is empty, running git command was skipped.", directory.getText());
                DialogFactory.createErrorAlert("Invalid directory", "Directory can't be empty");
                return;
            }

            AppConfig.getInstance().setLastRunFolder(directory.getText());

            new Thread(() -> {
                searchAndExecuteGitCommand(directory.getText(), gitCommand.getText());
                run.setDisable(false);
            }).start();
        }
    }

    public void setGitCommand(CommandDto commandDto) {
        gitCommand.setText(commandDto.getCommand());
        gitComment.setText(commandDto.getComment());
        savedCommand = commandDto;
        switchToSavedCommand(true);
    }

    public void removeSavedCommand(CommandDto commandDto) {
        if (Objects.equals(commandDto, savedCommand)) {
            savedCommand = null;
            switchToSavedCommand(false);
        }
    }

    private void switchToSavedCommand(boolean switchToSavedCommand) {
        update.setDisable(!switchToSavedCommand);
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
            LOGGER.error("Specified folder either don't exist or isn't a directory, running git command was skipped.");
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
        File tempDir = new File("temp");
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            LOGGER.error("Failed to create the temp directory '{}'.", "temp");
        }
        File scriptFile = File.createTempFile("script", ".sh", tempDir);
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