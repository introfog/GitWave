package com.github.introfog.rgit.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

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

    public static void createCloseConfirmationAlert(Stage primaryStage) {
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Do you really want to close the application?");
        confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                primaryStage.close();
            }
        });
    }
}
