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

import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public abstract class BaseController {
    protected FxmlStageHolder fxmlStageHolder;

    public void initialize(FxmlStageHolder fxmlStageHolder) {
        this.fxmlStageHolder = fxmlStageHolder;
    }

    protected void setClosingOnEscapePressing(FxmlStageHolder fxmlStageHolder) {
        fxmlStageHolder.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fxmlStageHolder.getStage().close();
            }
        });
    }

    protected void closeStage() {
        fxmlStageHolder.getStage().close();
    }

    protected Stage getStage() {
        return fxmlStageHolder.getStage();
    }
}
