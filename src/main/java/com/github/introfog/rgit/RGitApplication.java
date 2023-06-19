package com.github.introfog.rgit;

import com.github.introfog.rgit.model.ProcessRunResult;
import com.github.introfog.rgit.model.ProcessRunnerUtil;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class RGitApplication extends Application {
    private static final double FIXED_WIDTH = 700;
    private static final double FIXED_HEIGHT = 700;

    @Override
    public void start(Stage stage) throws IOException {
        boolean isGitInstalled = checkIfGitInstalledAndAvailable();

        if (isGitInstalled) {
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
    }

    private boolean checkIfGitInstalledAndAvailable() {
        try {
            ProcessRunResult result = ProcessRunnerUtil.runProcess(new String[] {"git", "--version"}, null);

            String line = result.getOutputLog();
            if (line != null && !line.contains("git version")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("rGit");
                alert.setHeaderText("Git not found");
                alert.setContentText("Make sure that Git is installed and path to git.exe is in PATH environment variable and re-run rGit.");
                alert.showAndWait();
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {
        Application.launch();
    }
}