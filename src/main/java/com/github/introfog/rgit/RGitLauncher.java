package com.github.introfog.rgit;

import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.StagesUtil;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RGitLauncher extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(RGitLauncher.class);

    @Override
    public void start(Stage stage) {
        if (!AppConfig.getInstance().isPathToGitSpecified()) {
            // TODO run setup only if person click on Run without bash specifying or open settings
            StagesUtil.setUpModalStage("view/setup.fxml", "Setup").getStage().showAndWait();
        }
        if (AppConfig.getInstance().isPathToGitSpecified()) {
            LOGGER.info("Path to GitBash.exe specified to '{}'", AppConfig.getInstance().getPathToGitBashExe());
            StagesUtil.setUpStage("view/executor.fxml", "rGit", stage).getStage().show();
        }
    }

    public static void main(String[] args) {
        Application.launch();
    }
}