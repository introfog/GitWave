package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AlertsUtil;
import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;
import com.github.introfog.rgit.model.dto.CommandDto;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveController extends BaseController {
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

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        fxmlStageHolder.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.save();
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                fxmlStageHolder.getStage().close();
            }
        });
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
            final CommandDto commandDto = new CommandDto(command.getText(), comment.getText());
            if (AppConfig.getInstance().containsCommand(commandDto)) {
                AlertsUtil.createErrorAlert("Save error", "The same command already exists");
            } else {
                exploreController.addNewCommand(commandDto);
                closeStage();
            }
        } else {
            LOGGER.error("Save controller isn't called correctly, explore and execute controller are null.");
        }
    }

    private void closeStage() {
        Stage modalStage = (Stage) comment.getScene().getWindow();
        modalStage.close();
    }
}
