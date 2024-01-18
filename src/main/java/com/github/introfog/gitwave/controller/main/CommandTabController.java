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

package com.github.introfog.gitwave.controller.main;

import com.github.introfog.gitwave.controller.EditController;
import com.github.introfog.gitwave.controller.ExploreController;
import com.github.introfog.gitwave.controller.SupportController;
import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.CommandDto;

import java.util.Objects;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandTabController extends SupportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandTabController.class);
    private final TextField command;
    private final TextField description;
    private final Button save;
    private final ParametersTabController parametersTabController;

    public CommandTabController(FxmlStageHolder fxmlStageHolder, TextField command, TextField description, Button save, ParametersTabController parametersTabController) {
        super(fxmlStageHolder);
        this.command = command;
        this.description = description;
        this.save = save;
        this.parametersTabController = parametersTabController;
        // if command field is editable, it means that user works with not saved command
        this.command.textProperty().addListener((obs, oldText, newText) -> {
            this.parametersTabController.parseCommandParameters(newText);
            this.save.setDisable(newText.isEmpty());
        });
    }

    @Override
    public boolean isValid() {
        if (command.getText().isEmpty()) {
            LOGGER.warn("Command '{}' is empty, running git command was skipped.", command.getText());
            DialogFactory.createErrorAlert("Invalid command", "Command can't be empty.");
            return false;
        }
        return true;
    }

    public String getCommandWithParameters() {
        return parametersTabController.applyParameters(command.getText());
    }

    public void reset() {
        specifySourceCommand(null);
    }

    public void chooseFromSaved() {
        FxmlStageHolder exploreHolder = StageFactory.createModalExploreWindow();
        exploreHolder.getStage().showAndWait();
        ExploreController exploreController = exploreHolder.getFxmlLoader().getController();
        final CommandDto pickedItem = exploreController.getPickedItem();
        if (pickedItem != null) {
            specifySourceCommand(pickedItem);
        }
        for (CommandDto removed : exploreController.getRemovedItems()) {
            removeSourceCommand(removed);
        }
    }

    public void saveOrEditCommand() {
        final CommandDto commandDto = new CommandDto(command.getText(), description.getText());
        if ("Save".equals(save.getText())) {
            AppConfig.getInstance().addCommand(commandDto);
            specifySourceCommand(commandDto);
        } else {
            FxmlStageHolder editHolder = StageFactory.createModalEditWindow(commandDto);
            editHolder.getStage().showAndWait();
            EditController editController = editHolder.getFxmlLoader().getController();
            CommandDto result = editController.getResult();
            boolean isSaveAsNew = editController.isSaveAsNew();
            if (result != null) {
                if (isSaveAsNew) {
                    AppConfig.getInstance().addCommand(result);
                    specifySourceCommand(result);
                } else {
                    AppConfig.getInstance().updateExistedCommand(commandDto, result);
                    specifySourceCommand(result);
                }
            }
        }
    }

    private void specifySourceCommand(CommandDto commandDto) {
        if (commandDto == null) {
            command.clear();
            description.clear();

            switchSaveMode(true);
        } else {
            command.setText(commandDto.getCommand());
            parametersTabController.parseCommandParameters(commandDto.getCommand());
            description.setText(commandDto.getDescription());

            switchSaveMode(false);
        }
    }

    private void removeSourceCommand(CommandDto commandDto) {
        AppConfig.getInstance().removeCommand(commandDto);
        final CommandDto currentCommand = new CommandDto(command.getText(), description.getText());
        if (Objects.equals(commandDto, currentCommand)) {
            switchSaveMode(true);
        }
    }

    private void switchSaveMode(boolean isSaveMode) {
        save.setText(isSaveMode ? "Save" : "Edit");
        command.setEditable(isSaveMode);
        description.setEditable(isSaveMode);
    }
}
