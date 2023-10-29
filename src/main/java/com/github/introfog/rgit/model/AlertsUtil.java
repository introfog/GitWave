package com.github.introfog.rgit.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class AlertsUtil {
    private AlertsUtil() {
        // private constructor
    }

    public static void createErrorAlert(String header, String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("rGit error");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
