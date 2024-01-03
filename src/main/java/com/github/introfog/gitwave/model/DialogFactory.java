/*
 * Copyright 2023-2024 Dmitry Chubrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.introfog.gitwave.model;

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
        stage.getIcons().add(new Image(StageFactory.class.getResourceAsStream(AppConstants.PATH_TO_LOGO)));
        alert.setTitle("GitWave error");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.getDialogPane().setStyle("-fx-font-family: verdana; -fx-font-size: 12");
        alert.showAndWait();
    }

    public static void createCloseConfirmationAlert(Stage primaryStage) {
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        Stage stage = (Stage) confirmationDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(StageFactory.class.getResourceAsStream(AppConstants.PATH_TO_LOGO)));
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Do you really want to close the application?");
        confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        confirmationDialog.getDialogPane().setStyle("-fx-font-family: verdana; -fx-font-size: 12");
        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                primaryStage.close();
            }
        });
    }

    public static ButtonType createSaveOrUpdateAlert() {
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        Stage stage = (Stage) confirmationDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(StageFactory.class.getResourceAsStream(AppConstants.PATH_TO_LOGO)));
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Do you want to save command as a new instance?\nSelect 'No' if update existed.");
        confirmationDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

        confirmationDialog.getDialogPane().setStyle("-fx-font-family: verdana; -fx-font-size: 12");
        ButtonType[] pressedButton = {null};
        confirmationDialog.showAndWait().ifPresent(response -> pressedButton[0] = response);
        return pressedButton[0];
    }
}
