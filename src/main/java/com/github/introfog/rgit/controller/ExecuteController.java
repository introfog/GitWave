package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.AppConstants;
import com.github.introfog.rgit.model.CommandExecutor;
import com.github.introfog.rgit.model.DialogFactory;
import com.github.introfog.rgit.model.StageFactory;
import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;
import com.github.introfog.rgit.model.dto.CommandDto;

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

    @FXML
    private TextField directory;

    @FXML
    private TextField command;

    @FXML
    private TextField comment;

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
        super.initialize(fxmlStageHolder);
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

        File selectedDirectory = directoryChooser.showDialog(getStage());
        if (selectedDirectory != null) {
            directory.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    protected void updateSavedCommand() {
        FxmlStageHolder holder = StageFactory.createModalStage("view/updater.fxml", "Command updater");

        UpdateController updateController = holder.getFxmlLoader().getController();
        updateController.setExecuteController(this);
        updateController.setCommand(savedCommand);

        holder.getStage().showAndWait();
    }

    @FXML
    protected void cleanSavedCommand() {
        command.clear();
        comment.clear();
        savedCommand = null;
        switchToSavedCommand(false);
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
        if (command.getText().isEmpty()) {
            DialogFactory.createErrorAlert("Invalid command", "Command can't be empty");
        } else {
            final CommandDto commandDto = new CommandDto(command.getText(), comment.getText());
            AppConfig.getInstance().addCommand(commandDto);
            setCommand(commandDto);
        }
    }

    @FXML
    protected void runCommand() {
        final String pathToGitBashExe = AppConfig.getInstance().getPathToGitBashExe();
        if (pathToGitBashExe == null || pathToGitBashExe.isEmpty()) {
            StageFactory.createModalStage("view/settings.fxml", "Settings").getStage().showAndWait();
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
        StageFactory.createModalStage("view/settings.fxml", "Settings").getStage().showAndWait();
    }

    @FXML
    protected void openAbout() {
        StageFactory.createModalStage("view/about.fxml", "About").getStage().showAndWait();
    }

    @FXML
    protected void findIssue() {
        AppConfig.getInstance().getHostServices().showDocument(AppConstants.LINK_TO_GIT_CONTRIBUTING_FILE);
    }

    void setCommand(CommandDto commandDto) {
        command.setText(commandDto.getCommand());
        comment.setText(commandDto.getComment());
        savedCommand = commandDto;
        switchToSavedCommand(true);
    }

    void removeSavedCommand(CommandDto commandDto) {
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
}