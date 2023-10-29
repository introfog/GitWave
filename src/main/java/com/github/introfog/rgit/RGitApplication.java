package com.github.introfog.rgit;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RGitApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        openTempWindow(stage);

        // TODO define a path to GitBash
        FXMLLoader fxmlLoader = new FXMLLoader(RGitApplication.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("rGit");
        stage.setScene(scene);
        // TODO make design flexible and allow resizing
        stage.setResizable(false);
        stage.show();


    }

    private void openTempWindow(Stage parentStage) throws IOException {
        Stage stage = new Stage();
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader fxmlLoader = new FXMLLoader(RGitApplication.class.getResource("setup.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("rGit setup");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}