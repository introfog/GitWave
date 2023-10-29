package com.github.introfog.rgit;

import com.github.introfog.rgit.model.SettingsManager;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RGitApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        if (!SettingsManager.isPathToGitSpecifiedInRegistry()) {
            openSetupWindow(stage);
        }
        openMainWindow(stage);
    }

    private void openMainWindow(Stage stage) throws IOException {
        // TODO define a path to GitBash
        FXMLLoader fxmlLoader = new FXMLLoader(RGitApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("rGit");
        stage.setScene(scene);
        // TODO make design flexible and allow resizing
        stage.setResizable(false);
        stage.show();
    }

    private void openSetupWindow(Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RGitApplication.class.getResource("setup.fxml"));
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