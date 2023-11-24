package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AlertsUtil;
import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.dto.CommandDto;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditController {
    // TODO when press `Enter` save the command, `Esc` cancel, the same for other windows
    private static final Logger LOGGER = LoggerFactory.getLogger(EditController.class);

    @FXML
    private TextField command;
    // TODO what if define long command or comment? Set some limit.
    @FXML
    private TextField comment;

    private CommandDto initialCommand;

    private ExecuteController executeController;

    public void setExecuteController(ExecuteController executeController) {
        this.executeController = executeController;
    }

    @FXML
    protected void cancel() {
        closeStage();
    }

    @FXML
    protected void saveAsNew() {
        if (command.getText().isEmpty()) {
            AlertsUtil.createErrorAlert("Invalid command", "Command can't be empty");
        } else if (executeController != null) {
            AppConfig.getInstance().addCommand(command.getText(), comment.getText());
            executeController.setGitCommand(new CommandDto(command.getText(), comment.getText()));
            closeStage();
        } else {
            LOGGER.error("Edit controller isn't called correctly, execute controller are null.");
        }
    }

    @FXML
    protected void updateExisted() {
        if (command.getText().isEmpty()) {
            AlertsUtil.createErrorAlert("Invalid command", "Command can't be empty");
        } else if (executeController != null) {
            AppConfig.getInstance().updateExistedCommand(initialCommand, command.getText(), comment.getText());
            executeController.setGitCommand(new CommandDto(command.getText(), comment.getText()));
            closeStage();
        } else {
            LOGGER.error("Edit controller isn't called correctly, execute controller are null.");
        }
    }

    public void setCommand(CommandDto commandDto) {
        this.initialCommand = commandDto;
        command.setText(commandDto.getCommand());
        comment.setText(commandDto.getComment());
    }

    private void closeStage() {
        Stage modalStage = (Stage) comment.getScene().getWindow();
        modalStage.close();
    }
}
