package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.RGitConfiguration;
import com.github.introfog.rgit.model.AlertsUtil;
import com.github.introfog.rgit.model.dto.CommandDto;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveController.class);

    @FXML
    private TextField command;
    // TODO what if define long command or comment? Set some limit.
    @FXML
    private TextField comment;

    private SavedController savedController;

    private MainController mainController;

    public void setSavedController(SavedController savedController) {
        this.savedController = savedController;
        if (savedController != null) {
            setMainController(null);
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        if (mainController != null) {
            setSavedController(null);
        }
    }

    @FXML
    protected void cancel() {
        closeStage();
    }

    @FXML
    protected void save() {
        if (command.getText().isEmpty()) {
            AlertsUtil.createErrorAlert("Invalid command", "Command can't be empty");
        } else if (mainController != null) {
            RGitConfiguration.getInstance().addCommandToConfig(command.getText(), comment.getText());
            closeStage();
        } else if (savedController != null){
            savedController.addNewCommand(new CommandDto(command.getText(), comment.getText()));
            closeStage();
        } else {
            LOGGER.error("Save controller isn't called correctly, saved and main controller are null.");
        }
    }

    public void setCommand(String commandText) {
        command.setText(commandText);
    }

    private void closeStage() {
        Stage modalStage = (Stage) comment.getScene().getWindow();
        modalStage.close();
    }
}
