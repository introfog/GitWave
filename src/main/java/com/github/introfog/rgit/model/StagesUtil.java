package com.github.introfog.rgit.model;

import com.github.introfog.rgit.RGitLauncher;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StagesUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StagesUtil.class);

    private StagesUtil() {
        // private constructor
    }

    public static FxmlStageHolder setUpModalStage(String fxmlPath, String title) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        final FxmlStageHolder fxmlStageHolder = setUpStage(fxmlPath, title, modalStage);
        fxmlStageHolder.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fxmlStageHolder.getStage().close();
            }
        });
        return fxmlStageHolder;
    }

    public static FxmlStageHolder setUpPrimaryStage(String fxmlPath, String title, Stage stage) {
        final FxmlStageHolder fxmlStageHolder = setUpStage(fxmlPath, title, stage);

        final Stage primaryStage = fxmlStageHolder.getStage();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            AlertsUtil.createCloseConfirmationAlert(primaryStage);
        });

        return fxmlStageHolder;
    }

    private static FxmlStageHolder setUpStage(String fxmlPath, String title, Stage stage) {
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
        return new FxmlStageHolder(stage, fxmlLoader, scene);
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
