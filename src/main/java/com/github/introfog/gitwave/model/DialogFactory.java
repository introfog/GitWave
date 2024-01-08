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
    private static final String COMMON_STYLES = "-fx-font-family: verdana; -fx-font-size: 12";
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
        alert.getDialogPane().setStyle(COMMON_STYLES);
        alert.showAndWait();
    }

    public static ButtonType createCloseConfirmationAlert() {
        Alert dialog = new Alert(AlertType.CONFIRMATION);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(StageFactory.class.getResourceAsStream(AppConstants.PATH_TO_LOGO)));
        dialog.setTitle("Confirmation");
        dialog.setHeaderText("Do you really want to close the application?");
        dialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.getDialogPane().setStyle(COMMON_STYLES);
        ButtonType[] pressedButton = {null};
        dialog.showAndWait().ifPresent(response -> pressedButton[0] = response);
        return pressedButton[0];
    }

    public static ButtonType createSaveOrUpdateAlert() {
        Alert dialog = new Alert(AlertType.CONFIRMATION);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(StageFactory.class.getResourceAsStream(AppConstants.PATH_TO_LOGO)));

        dialog.setTitle("Confirmation");
        dialog.setHeaderText("Do you want to save command as a new instance?\nSelect 'No' if update existed.");
        dialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

        dialog.getDialogPane().setStyle(COMMON_STYLES);
        ButtonType[] pressedButton = {null};
        dialog.showAndWait().ifPresent(response -> pressedButton[0] = response);
        return pressedButton[0];
    }
}
