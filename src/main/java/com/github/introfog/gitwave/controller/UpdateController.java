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
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.CommandDto;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateController.class);

    @FXML
    private TextField command;

    @FXML
    private TextField description;

    private CommandDto initialCommand;

    private ExecuteController executeController;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        super.setClosingOnEscapePressing(fxmlStageHolder);
    }

    @FXML
    protected void cancel() {
        closeStage();
    }

    @FXML
    protected void saveAsNew() {
        if (command.getText().isEmpty()) {
            DialogFactory.createErrorAlert("Invalid command", "Command can't be empty");
        } else if (executeController != null) {
            final CommandDto commandDto = new CommandDto(command.getText(), description.getText());
            if (commandDto.equals(initialCommand)) {
                DialogFactory.createErrorAlert("Save error", "The same command already exists");
            } else {
                AppConfig.getInstance().addCommand(commandDto);
                executeController.setCommand(commandDto);
                closeStage();
            }
        } else {
            LOGGER.error("Edit controller isn't called correctly, execute controller are null.");
        }
    }

    @FXML
    protected void updateExisted() {
        if (command.getText().isEmpty()) {
            DialogFactory.createErrorAlert("Invalid command", "Command can't be empty");
        } else if (executeController != null) {
            final CommandDto currentCommand = new CommandDto(command.getText(), description.getText());
            AppConfig.getInstance().updateExistedCommand(initialCommand, currentCommand);
            executeController.setCommand(currentCommand);
            closeStage();
        } else {
            LOGGER.error("Edit controller isn't called correctly, execute controller are null.");
        }
    }

    void setExecuteController(ExecuteController executeController) {
        this.executeController = executeController;
    }

    void setCommand(CommandDto commandDto) {
        this.initialCommand = commandDto;
        command.setText(commandDto.getCommand());
        description.setText(commandDto.getDescription());
    }
}
