package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.RGitConfiguration;
import com.github.introfog.rgit.model.AlertsUtil;

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

    @FXML
    protected void cancel() {
        closeStage();
    }

    @FXML
    protected void save() {
        if (command.getText().isEmpty()) {
            AlertsUtil.createErrorAlert("Invalid command", "Command can't be empty");
        } else {
            RGitConfiguration.getInstance().addCommandToConfig(command.getText(), comment.getText());
            closeStage();
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
