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

import com.github.introfog.gitwave.controller.ExploreController;
import com.github.introfog.gitwave.controller.SupportController;
import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.CommandDto;

import java.util.Objects;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandTabController extends SupportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandTabController.class);
    private static final String GREEN_TEXT_CSS_STYLE = "-fx-text-fill: green";
    private static final String RED_TEXT_CSS_STYLE = "-fx-text-fill: red";
    private static final String BLACK_TEXT_CSS_STYLE = "-fx-text-fill: black";
    private final TextField command;
    private final TextField description;
    private final Button save;
    private final ParametersTabController parametersTabController;
    private CommandDto sourceCommand;

    public CommandTabController(FxmlStageHolder fxmlStageHolder, TextField command, TextField description, Button save, ParametersTabController parametersTabController) {
        super(fxmlStageHolder);
        this.command = command;
        this.description = description;
        this.save = save;
        this.parametersTabController = parametersTabController;
        setUpSaveIndication();
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

    public void clean() {
        specifySourceCommand(null);
    }

    public void chooseFromSaved() {
        FxmlStageHolder holder = StageFactory.createModalExploreWindow();
        holder.getStage().showAndWait();
        ExploreController exploreController = holder.getFxmlLoader().getController();
        final CommandDto pickedItem = exploreController.getPickedItem();
        if (pickedItem != null) {
            specifySourceCommand(pickedItem);
        }
        for (CommandDto removed : exploreController.getRemovedItems()) {
            removeSourceCommand(removed);
        }
    }

    public void saveCommand() {
        final CommandDto commandDto = new CommandDto(command.getText(), description.getText());
        if (sourceCommand == null) {
            AppConfig.getInstance().addCommand(commandDto);
            specifySourceCommand(commandDto);
            // TODO MINOR if DTO is already existed, nothing was happened, is it OK?
        } else {
            ButtonType result = DialogFactory.createSaveOrUpdateAlert();
            if (ButtonType.YES == result) {
                AppConfig.getInstance().addCommand(commandDto);
                specifySourceCommand(commandDto);
            } else if (ButtonType.NO == result) {
                AppConfig.getInstance().updateExistedCommand(sourceCommand, commandDto);
                specifySourceCommand(commandDto);
            }
        }
    }

    private void setUpSaveIndication() {
        command.textProperty().addListener((obs, oldText, newText) -> {
            parametersTabController.parseCommandParameters(newText);
            if (sourceCommand != null) {
                updateSaveIndication(newText, command, true);
            } else {
                save.setDisable(newText.isEmpty());
            }
        });
        description.textProperty().addListener((obs, oldText, newText) -> {
            if (sourceCommand != null) {
                updateSaveIndication(newText, description, false);
            }
        });
    }

    private void updateSaveIndication(String currentMainText, TextField field, boolean isCommand) {
        final String sourceMainText = isCommand ? sourceCommand.getCommand() : sourceCommand.getDescription();
        final String sourceSecText = isCommand ? sourceCommand.getDescription() : sourceCommand.getCommand();
        final String currentSecText = isCommand ? description.getText() : command.getText();

        if (sourceMainText.equals(currentMainText)){
            field.setStyle(GREEN_TEXT_CSS_STYLE);
            if (sourceSecText.equals(currentSecText)) {
                save.setDisable(true);
            }
        } else {
            save.setDisable(false);
            field.setStyle(RED_TEXT_CSS_STYLE);
        }
    }

    private void specifySourceCommand(CommandDto commandDto) {
        save.setDisable(true);
        sourceCommand = commandDto;
        if (commandDto == null) {
            command.clear();
            command.setStyle(BLACK_TEXT_CSS_STYLE);
            description.clear();
            description.setStyle(BLACK_TEXT_CSS_STYLE);
        } else {
            command.setText(commandDto.getCommand());
            parametersTabController.parseCommandParameters(commandDto.getCommand());
            command.setStyle(GREEN_TEXT_CSS_STYLE);
            description.setText(commandDto.getDescription());
            description.setStyle(GREEN_TEXT_CSS_STYLE);
        }
    }

    private void removeSourceCommand(CommandDto commandDto) {
        AppConfig.getInstance().removeCommand(commandDto);
        if (Objects.equals(commandDto, sourceCommand)) {
            sourceCommand = null;
            command.setStyle(BLACK_TEXT_CSS_STYLE);
            description.setStyle(BLACK_TEXT_CSS_STYLE);
            save.setDisable(false);
        }
    }
}
