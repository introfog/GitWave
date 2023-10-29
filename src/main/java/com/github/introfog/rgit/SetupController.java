package com.github.introfog.rgit;

import com.github.introfog.rgit.model.AlertsUtil;
import com.github.introfog.rgit.model.SettingsManager;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SetupController {
    private static final String GIT_HUB_URI = "https://github.com/introfog/rGit";

    @FXML
    private AnchorPane anchorSetup;

    @FXML
    private TextField pathToBashExe;

    @FXML
    protected void finishSetup() {
        Stage stage = (Stage) anchorSetup.getScene().getWindow();
        File bashExeFile = new File(pathToBashExe.getText());
        if (bashExeFile.exists() && bashExeFile.getAbsolutePath().endsWith(".exe")) {
            SettingsManager.setPathToGitBashExeInRegistry(bashExeFile.getAbsolutePath());
            stage.close();
        } else {
            AlertsUtil.createErrorAlert("Git Bash executable hasn't been specified",
                    "Git Bash executable hasn't been specified correctly. Either specify path manually or find via file browser.");
        }
    }

    @FXML
    protected void browseGitBashExe() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter exeFilter = new FileChooser.ExtensionFilter("Bash executable (*.exe)", "*.exe");
        fileChooser.getExtensionFilters().add(exeFilter);

        Stage stage = (Stage) anchorSetup.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null && selectedFile.exists()) {
            pathToBashExe.setText(selectedFile.getAbsolutePath());
        } else {
            AlertsUtil.createErrorAlert("Provided file wasn't found", "Provided file wasn't found, try again");
        }
    }

    @FXML
    protected void goToGitHub() {
        try {
            Desktop.getDesktop().browse(new URI(GIT_HUB_URI));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
