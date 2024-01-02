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
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.CommandDto;

import javafx.fxml.FXML;

public class UpdateController extends BaseController {
    private CommandDto oldCommand;
    private CommandDto newCommand;
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
        AppConfig.getInstance().addCommand(newCommand);
        executeController.specifySourceCommand(newCommand);
        closeStage();
    }

    @FXML
    protected void updateExisted() {
        AppConfig.getInstance().updateExistedCommand(oldCommand, newCommand);
        executeController.specifySourceCommand(newCommand);
        closeStage();
    }

    void setRequiredFields(ExecuteController executeController, CommandDto oldCommand, CommandDto newCommand) {
        this.executeController = executeController;
        this.oldCommand = oldCommand;
        this.newCommand = newCommand;
    }
}
