package com.github.introfog.rgit;

import com.github.introfog.rgit.model.StageFactory;

import javafx.application.Application;
import javafx.stage.Stage;

public class RGitLauncher extends Application {
    @Override
    public void start(Stage stage) {
        // TODO create a icon for the app (use also in exe)
        StageFactory.createPrimaryStage("view/executor.fxml", "rGit", stage).getStage().show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}