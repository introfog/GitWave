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

package com.github.introfog.gitwave.controller;

import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.AppConstants;
import com.github.introfog.gitwave.model.CommandExecutor;
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.CommandDto;

import java.io.File;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteController.class);
    private static final String GREEN_TEXT_CSS_STYLE = "-fx-text-fill: green";
    private static final String RED_TEXT_CSS_STYLE = "-fx-text-fill: red";
    private static final String BLACK_TEXT_CSS_STYLE = "-fx-text-fill: black";

    @FXML
    private TextField directory;

    @FXML
    private TextField command;

    @FXML
    private TextField description;

    @FXML
    private Button save;

    private CommandDto sourceCommand;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        final Stage primaryStage = fxmlStageHolder.getStage();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            DialogFactory.createCloseConfirmationAlert(primaryStage);
        });

        command.textProperty().addListener((obs, oldText, newText) -> {
            if (sourceCommand != null) {
                updateFields(newText, command, true);
            } else {
                save.setDisable(newText.isEmpty());
            }
        });
        description.textProperty().addListener((obs, oldText, newText) -> {
            if (sourceCommand != null) {
                updateFields(newText, description, false);
            }
        });
    }

    private void updateFields(String currentMainText, TextField field, boolean isCommand) {
        final String sourceMainText = isCommand ? sourceCommand.getCommand() : sourceCommand.getDescription();
        final String sourceSecText = isCommand ? sourceCommand.getDescription() : sourceCommand.getCommand();
        final String currentSecText = isCommand ? description.getText() : command.getText();

        if (sourceMainText.equals(currentMainText)){
            field.setStyle(GREEN_TEXT_CSS_STYLE);
            if (sourceSecText.equals(currentSecText)) {
                save.setDisable(true);
            }
        } else {
            save.setDisable(false);
            field.setStyle(RED_TEXT_CSS_STYLE);
        }
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

        File selectedDirectory = directoryChooser.showDialog(getStage());
        if (selectedDirectory != null) {
            directory.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    protected void clean() {
        specifySourceCommand(null);
    }

    @FXML
    protected void chooseFromSaved() {
        FxmlStageHolder holder = StageFactory.createModalExploreWindow();

        ExploreController exploreController = holder.getFxmlLoader().getController();
        exploreController.setExecuteController(this);

        holder.getStage().showAndWait();
    }

    @FXML
    protected void saveCommand() {
        final CommandDto commandDto = new CommandDto(command.getText(), description.getText());
        if (sourceCommand == null) {
            AppConfig.getInstance().addCommand(commandDto);
            specifySourceCommand(commandDto);
            // TODO MINOR if DTO is already existed, nothing was happened, is it OK?
        } else {
            FxmlStageHolder holder = StageFactory.createModalStage("view/updater.fxml", "Command updater");
            UpdateController updateController = holder.getFxmlLoader().getController();
            // TODO add required methods for all stages where necessary
            updateController.setRequiredFields(this, sourceCommand, commandDto);
            holder.getStage().showAndWait();
        }
    }

    @FXML
    protected void runCommand() {
        final String pathToGitBashExe = AppConfig.getInstance().getPathToGitBashExe();
        if (pathToGitBashExe == null || pathToGitBashExe.isEmpty()) {
            StageFactory.createModalSettingsWindow().getStage().showAndWait();
        } else if (!(new File(pathToGitBashExe)).exists()) {
            LOGGER.error("Specified GitBash.exe path '{}' points to not-existent file, running git command was skipped.", pathToGitBashExe);
            DialogFactory.createErrorAlert("Invalid path to GitBash.exe", "Specified path \"" + pathToGitBashExe +
                    "\" points to not-existent file. Specify correct path in settings.");
        } else {
            if (command.getText().isEmpty()) {
                LOGGER.warn("Command '{}' is empty, running git command was skipped.", command.getText());
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
                CommandExecutor.searchGitReposAndExecuteCommand(directory.getText(), command.getText());
            }).start();
        }
    }

    @FXML
    protected void openSettings() {
        StageFactory.createModalSettingsWindow().getStage().showAndWait();
    }

    @FXML
    protected void openAbout() {
        StageFactory.createModalAboutWindow().getStage().showAndWait();
    }

    @FXML
    protected void foundIssue() {
        AppConfig.getInstance().getHostServices().showDocument(AppConstants.LINK_TO_GIT_CONTRIBUTING_FILE);
    }

    void specifySourceCommand(CommandDto commandDto) {
        save.setDisable(true);
        sourceCommand = commandDto;
        if (commandDto == null) {
            command.clear();
            command.setStyle(BLACK_TEXT_CSS_STYLE);
            description.clear();
            description.setStyle(BLACK_TEXT_CSS_STYLE);
        } else {
            command.setText(commandDto.getCommand());
            command.setStyle(GREEN_TEXT_CSS_STYLE);
            description.setText(commandDto.getDescription());
            description.setStyle(GREEN_TEXT_CSS_STYLE);
        }
    }

    void removeSavedCommand(CommandDto commandDto) {
        if (Objects.equals(commandDto, sourceCommand)) {
            sourceCommand = null;
            command.setStyle(BLACK_TEXT_CSS_STYLE);
            description.setStyle(BLACK_TEXT_CSS_STYLE);
            save.setDisable(false);
        }
    }
}