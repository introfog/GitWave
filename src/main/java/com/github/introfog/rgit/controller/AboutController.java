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
import com.github.introfog.rgit.model.AppConstants;
import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutController extends BaseController {
    @FXML
    private Label version;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        super.setClosingOnEscapePressing(fxmlStageHolder);
        version.setText(AppConstants.VERSION);
    }

    @FXML
    protected void close() {
        closeStage();
    }

    @FXML
    protected void openGitHub() {
        AppConfig.getInstance().getHostServices().showDocument(AppConstants.LINK_TO_GIT_REPO);
    }
}
