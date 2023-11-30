package com.github.introfog.rgit;

import com.github.introfog.rgit.model.StageFactory;

import javafx.application.Application;
import javafx.stage.Stage;

public class RGitLauncher extends Application {
    @Override
    public void start(Stage stage) {
        StageFactory.createPrimaryStage("view/executor.fxml", "rGit", stage).getStage().show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}