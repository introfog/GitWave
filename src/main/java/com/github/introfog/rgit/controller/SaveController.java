package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AlertsUtil;
import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.dto.CommandDto;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveController {
    // TODO when press `Enter` save the command, `Esc` cancel, the same for other windows
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveController.class);

    @FXML
    private TextField command;
    // TODO what if define long command or comment? Set some limit.
    @FXML
    private TextField comment;

    private ExploreController exploreController;

    private ExecuteController executeController;

    public void setSavedController(ExploreController exploreController) {
        this.exploreController = exploreController;
        if (exploreController != null) {
            setMainController(null);
        }
    }

    public void setMainController(ExecuteController executeController) {
        this.executeController = executeController;
        if (executeController != null) {
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
        } else if (executeController != null) {
            AppConfig.getInstance().addCommand(command.getText(), comment.getText());
            closeStage();
        } else if (exploreController != null){
            exploreController.addNewCommand(new CommandDto(command.getText(), comment.getText()));
            closeStage();
        } else {
            LOGGER.error("Save controller isn't called correctly, saved and main controller are null.");
        }
    }

    public void setCommand(String commandText) {
        command.setText(commandText);
    }

    public void setComment(String commentText) {
        comment.setText(commentText);
    }

    private void closeStage() {
        Stage modalStage = (Stage) comment.getScene().getWindow();
        modalStage.close();
    }
}
