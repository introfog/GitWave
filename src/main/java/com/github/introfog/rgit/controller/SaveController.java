package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AlertsUtil;
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

    public void setExploreController(ExploreController exploreController) {
        this.exploreController = exploreController;
    }

    @FXML
    protected void cancel() {
        closeStage();
    }

    @FXML
    protected void save() {
        if (command.getText().isEmpty()) {
            AlertsUtil.createErrorAlert("Invalid command", "Command can't be empty");
        } else if (exploreController != null){
            exploreController.addNewCommand(new CommandDto(command.getText(), comment.getText()));
            closeStage();
        } else {
            LOGGER.error("Save controller isn't called correctly, explore and execute controller are null.");
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
