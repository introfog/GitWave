package com.github.introfog.rgit;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class HelloController {
    @FXML
    private TextField directory;

    @FXML
    private AnchorPane anchorid;

    @FXML
    protected void browseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) anchorid.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            directory.setText(selectedDirectory.getAbsolutePath());
        }
    }
}