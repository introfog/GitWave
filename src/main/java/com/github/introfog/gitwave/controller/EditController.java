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

import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.CommandDto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditController.class);

    @FXML
    private TextField command;
    @FXML
    private TextField description;
    @FXML
    private Button save;

    private CommandDto result;
    private boolean saveAsNew;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        super.setClosingOnEscapePressing(fxmlStageHolder);
        save.requestFocus();
    }

    public void setCommand(CommandDto commandDto) {
        command.setText(commandDto.getCommand());
        description.setText(commandDto.getDescription());
    }

    public CommandDto getResult() {
        return result;
    }

    public boolean isSaveAsNew() {
        return saveAsNew;
    }

    @FXML
    protected void saveAsNew() {
        saveAsNew = true;
        if (tryToSaveCommand()) {
            closeStage();
        }
    }

    @FXML
    protected void updateExisted() {
        saveAsNew = false;
        if (tryToSaveCommand()) {
            closeStage();
        }
    }

    private boolean tryToSaveCommand() {
        if (command.getText().isEmpty()) {
            LOGGER.warn("Command '{}' is empty, command editing was skipped.", command.getText());
            DialogFactory.createErrorAlert("Invalid command", "Command can't be empty.");
            return false;
        } else {
            result = new CommandDto(command.getText(), description.getText());
            return true;
        }
    }
}
