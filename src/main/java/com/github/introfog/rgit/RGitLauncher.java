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

package com.github.introfog.rgit;

import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.StageFactory;

import javafx.application.Application;
import javafx.stage.Stage;

public class RGitLauncher extends Application {
    @Override
    public void start(Stage stage) {
        StageFactory.createPrimaryStage("view/executor.fxml", "rGit", stage).getStage().show();
        AppConfig.getInstance().setHostServices(getHostServices());
        // TODO java image already has conf, legal and other folder, let's put them into some javaImage folder
    }

    public static void main(String[] args) {
        Application.launch();
    }
}