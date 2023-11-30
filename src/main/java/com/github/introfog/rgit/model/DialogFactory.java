package com.github.introfog.rgit.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public final class DialogFactory {
    private DialogFactory() {
        // private constructor
    }

    public static void createErrorAlert(String header, String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(StageFactory.class.getResourceAsStream("/logo.png")));
        alert.setTitle("rGit error");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void createCloseConfirmationAlert(Stage primaryStage) {
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        Stage stage = (Stage) confirmationDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(StageFactory.class.getResourceAsStream("/logo.png")));
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
