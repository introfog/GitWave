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

package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.DialogFactory;
import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;
import com.github.introfog.rgit.model.dto.CommandDto;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveController.class);

    @FXML
    private TextField command;
    // TODO what if define long command or description? Set some limit.
    @FXML
    private TextField description;

    private ExploreController exploreController;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
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
            DialogFactory.createErrorAlert("Invalid command", "Command can't be empty");
        } else if (exploreController != null){
            final CommandDto commandDto = new CommandDto(command.getText(), description.getText());
            if (AppConfig.getInstance().containsCommand(commandDto)) {
                DialogFactory.createErrorAlert("Save error", "The same command already exists");
            } else {
                exploreController.addNewCommand(commandDto);
                closeStage();
            }
        } else {
            LOGGER.error("Save controller isn't called correctly, explore and execute controller are null.");
        }
    }

    void setExploreController(ExploreController exploreController) {
        this.exploreController = exploreController;
    }
}
