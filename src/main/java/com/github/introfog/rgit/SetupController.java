package com.github.introfog.rgit;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SetupController {
    @FXML
    private AnchorPane anchorSetup;

    @FXML
    private Button finish;

    @FXML
    protected void finishSetup() {
        Stage stage = (Stage) anchorSetup.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void goToGitHub() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/introfog/rGit"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
