package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.DialogFactory;
import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetupController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetupController.class);

    private static final String GIT_HUB_URI = "https://github.com/introfog/rGit";

    @FXML
    private AnchorPane anchorSetup;

    @FXML
    private TextField pathToBashExe;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.setClosingOnEscapePressing(fxmlStageHolder);
    }

    @FXML
    protected void finishSetup() {
        Stage stage = (Stage) anchorSetup.getScene().getWindow();
        File bashExeFile = new File(pathToBashExe.getText());
        if (bashExeFile.exists() && bashExeFile.getAbsolutePath().endsWith(".exe")) {
            final String absolutePath = bashExeFile.getAbsolutePath();
            AppConfig.getInstance().setPathToGitBashExe(absolutePath);
            LOGGER.info("Path to GitBash.exe registered to '{}'", absolutePath);
            stage.close();
        } else {
            LOGGER.error("Wrong path to GitBash.exe '{}'", bashExeFile.getAbsolutePath());
            DialogFactory.createErrorAlert("Git Bash executable hasn't been specified",
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
            LOGGER.error("Wrong browsed path to GitBash.exe '{}'", selectedFile);
            DialogFactory.createErrorAlert("Provided file wasn't found", "Provided file wasn't found, try again");
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
