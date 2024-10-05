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

package com.github.introfog.gitwave;

import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.OsRecogniser;
import com.github.introfog.gitwave.model.StageFactory;

import javafx.application.Application;
import javafx.stage.Stage;

public class GitWaveLauncher extends Application {
    @Override
    public void start(Stage stage) {
        StageFactory.createPrimaryExecuteWindow(stage).getStage().show();
        AppConfig.getInstance().setHostServices(getHostServices());
        if (!OsRecogniser.isCurrentOsUnixLike()) {
            final String pathToGitBashExe = AppConfig.getInstance().getPathToGitBashExe();
            if (pathToGitBashExe == null || pathToGitBashExe.isEmpty()) {
                StageFactory.createModalSetupWindow().getStage().showAndWait();
            }
        }
    }

    public static void main(String[] args) {
        Application.launch();
    }
}