package com.github.introfog.rgit;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RGitApplication extends Application {
    private static final double FIXED_WIDTH = 600;
    private static final double FIXED_HEIGHT = 250;

    @Override
    public void start(Stage stage) throws IOException {
        // TODO define a path to GitBash
        FXMLLoader fxmlLoader = new FXMLLoader(RGitApplication.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("rGit");
        stage.setWidth(FIXED_WIDTH);
        stage.setHeight(FIXED_HEIGHT);
        stage.setScene(scene);
        // TODO make design flexible and allow resizing
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        Application.launch();
    }
}