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

package com.github.introfog.gitwave.model;

import com.github.introfog.gitwave.GitWaveLauncher;
import com.github.introfog.gitwave.controller.BaseController;
import com.github.introfog.gitwave.controller.EditController;
import com.github.introfog.gitwave.controller.main.ExecutionController;
import com.github.introfog.gitwave.model.dto.CommandDto;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StageFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(StageFactory.class);

    private static final Set<ExecutionController> executingControllers = new HashSet<>();

    private StageFactory() {
        // private constructor
    }

    public static void registerExecutionController(ExecutionController executionController) {
        executingControllers.add(executionController);
    }

    public static void unregisterExecutingController(ExecutionController executionController) {
        executingControllers.remove(executionController);
    }

    public static Set<ExecutionController> getExecutingControllers() {
        return executingControllers;
    }

    public static FxmlStageHolder createNoneModalExecutionWindow() {
        FxmlStageHolder holder = StageFactory.createNoneModalStage("view/execution.fxml", "Command execution result");
        holder.getStage().setMinWidth(200);
        holder.getStage().setMinHeight(200);
        int alreadyOpenedExecutors = getExecutingControllers().size();
        int shift = 20;
        holder.getStage().setX(100 + (alreadyOpenedExecutors * shift));
        holder.getStage().setY(100 + (alreadyOpenedExecutors * shift));
        registerExecutionController(holder.getFxmlLoader().getController());
        return holder;
    }

    public static FxmlStageHolder createModalExploreWindow() {
        FxmlStageHolder holder = StageFactory.createModalStage("view/explorer.fxml", "Command explorer");
        holder.getStage().setMinWidth(400);
        holder.getStage().setMinHeight(200);
        return holder;
    }

    public static FxmlStageHolder createModalEditWindow(CommandDto commandDto) {
        FxmlStageHolder holder = StageFactory.createModalStage("view/edit.fxml", "Command editor");
        holder.getStage().setMinWidth(400);
        holder.getStage().setMinHeight(210);
        EditController editController = holder.getFxmlLoader().getController();
        editController.setCommand(commandDto);
        return holder;
    }

    public static FxmlStageHolder createModalSettingsWindow() {
        FxmlStageHolder holder = StageFactory.createModalStage("view/settings.fxml", "Settings");
        holder.getStage().setMinWidth(500);
        holder.getStage().setMinHeight(240);
        return holder;
    }

    public static FxmlStageHolder createModalSetupWindow() {
        FxmlStageHolder holder = StageFactory.createModalStage("view/setup.fxml", "Setup");
        holder.getStage().setMinWidth(520);
        holder.getStage().setMinHeight(280);
        return holder;
    }

    public static FxmlStageHolder createModalAboutWindow() {
        FxmlStageHolder holder = StageFactory.createModalStage("view/about.fxml", "About");
        holder.getStage().setResizable(false);
        return holder;
    }

    public static FxmlStageHolder createModalUpdateWindow() {
        FxmlStageHolder holder = StageFactory.createModalStage("view/update.fxml", "New Version Available!");
        holder.getStage().setResizable(false);
        return holder;
    }

    public static FxmlStageHolder createPrimaryExecuteWindow(Stage stage) {
        final FxmlStageHolder holder = creteStage("view/main.fxml", "GitWave", stage);
        holder.getStage().setMinWidth(400);
        holder.getStage().setMinHeight(340);
        return holder;
    }

    private static FxmlStageHolder createModalStage(String fxmlPath, String title) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        return creteStage(fxmlPath, title, modalStage);
    }

    private static FxmlStageHolder createNoneModalStage(String fxmlPath, String title) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.NONE);
        return creteStage(fxmlPath, title, modalStage);
    }

    private static FxmlStageHolder creteStage(String fxmlPath, String title, Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(GitWaveLauncher.class.getResource(fxmlPath));
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
        stage.setResizable(true);
        stage.getIcons().add(new Image(StageFactory.class.getResourceAsStream(AppConstants.PATH_TO_LOGO_128)));
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
