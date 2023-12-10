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

package com.github.introfog.rgit.model;

import com.github.introfog.rgit.RGitLauncher;
import com.github.introfog.rgit.controller.BaseController;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StageFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(StageFactory.class);

    private StageFactory() {
        // private constructor
    }

    public static FxmlStageHolder createModalSaveWindow() {
        FxmlStageHolder holder = createModalStage("view/saver.fxml", "Command saver");
        holder.getStage().setMinWidth(300);
        holder.getStage().setMinHeight(220);
        holder.getStage().setMaxHeight(220);
        holder.getStage().setResizable(true);
        return holder;
    }

    public static FxmlStageHolder createModalStage(String fxmlPath, String title) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        return creteStage(fxmlPath, title, modalStage);
    }

    public static FxmlStageHolder createPrimaryStage(String fxmlPath, String title, Stage stage) {
        return creteStage(fxmlPath, title, stage);
    }

    private static FxmlStageHolder creteStage(String fxmlPath, String title, Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(RGitLauncher.class.getResource(fxmlPath));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            String fileName = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1);
            LOGGER.error("Something goes wrong while opening '" + fileName + "' resource.", e);
            throw new RuntimeException(e);
        }

        stage.setTitle(title);
        stage.setScene(scene);
        // TODO make design flexible and allow resizing
        stage.setResizable(false);
        stage.getIcons().add(new Image(StageFactory.class.getResourceAsStream(AppConstants.PATH_TO_LOGO)));
        BaseController controller = fxmlLoader.getController();
        final FxmlStageHolder fxmlStageHolder = new FxmlStageHolder(stage, fxmlLoader, scene);
        controller.initialize(fxmlStageHolder);
        return fxmlStageHolder;
    }

    public static final class FxmlStageHolder {
        private final Stage stage;
        private final FXMLLoader fxmlLoader;

        private final Scene scene;

        public FxmlStageHolder(Stage stage, FXMLLoader fxmlLoader, Scene scene) {
            this.stage = stage;
            this.fxmlLoader = fxmlLoader;
            this.scene = scene;
        }

        public Stage getStage() {
            return stage;
        }

        public FXMLLoader getFxmlLoader() {
            return fxmlLoader;
        }

        public Scene getScene() {
            return scene;
        }
    }
}
