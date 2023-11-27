package com.github.introfog.rgit.model;

import com.github.introfog.rgit.RGitLauncher;
import com.github.introfog.rgit.controller.BaseController;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StageFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(StageFactory.class);

    private StageFactory() {
        // private constructor
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
