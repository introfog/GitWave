package com.github.introfog.rgit;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RGitLauncher extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(RGitLauncher.class);

    @Override
    public void start(Stage stage) throws IOException {
        if (!RGitConfiguration.getInstance().isPathToGitSpecifiedInRegistry()) {
            openSetupWindow(stage);
        }
        if (RGitConfiguration.getInstance().isPathToGitSpecifiedInRegistry()) {
            LOGGER.info("Path to GitBash.exe specified to '{}'", RGitConfiguration.getInstance().getPathToGitBashExeInRegistry());
            openMainWindow(stage);
        }
    }

    private void openMainWindow(Stage stage) throws IOException {
        // TODO define a path to GitBash
        FXMLLoader fxmlLoader = new FXMLLoader(RGitLauncher.class.getResource("view/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("rGit");
        stage.setScene(scene);
        // TODO make design flexible and allow resizing
        stage.setResizable(false);
        stage.show();
    }

    private void openSetupWindow(Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RGitLauncher.class.getResource("view/setup.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = new Stage();
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("rGit setup");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}