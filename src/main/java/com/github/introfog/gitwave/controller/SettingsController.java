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

package com.github.introfog.gitwave.controller;

import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    @FXML
    private TextField pathToBash;

    @FXML
    private Button save;

    @FXML
    private Label done;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        super.setClosingOnEscapePressing(fxmlStageHolder);
        final String pathToGitBashStr = AppConfig.getInstance().getPathToBash();
        if (pathToGitBashStr != null) {
            pathToBash.setText(pathToGitBashStr);
        }
        save.requestFocus();
        if (done != null) {
            done.setVisible(false);
        }
    }

    @FXML
    protected void save() {
        if (AppConfig.getInstance().setPathToBash(pathToBash.getText())) {
            closeStage();
        } else {
            LOGGER.error("Wrong path to bash '{}'", pathToBash.getText());
            DialogFactory.createErrorAlert("Bash hasn't been specified",
                    "Bash hasn't been specified correctly. Either specify path manually or find via file browser.", 210);
        }
    }

    @FXML
    protected void cleanLogs() {
        done.setVisible(true);

        try {
            Files.walk(Paths.get("logs"))
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (FileSystemException e) {
                        LOGGER.info("Log file '{}' wasn't removed because it is already used by logger.", path);
                    } catch (IOException e) {
                        LOGGER.error("An error occurred while cleaning logs folder on '" + path + "' file.", e);
                    }
                });
        } catch (IOException e) {
            LOGGER.error("An error occurred while cleaning logs folder.", e);
        }

        PauseTransition visibleDone = new PauseTransition(Duration.seconds(1.5));
        visibleDone.setOnFinished(event -> done.setVisible(false));
        visibleDone.play();
    }

    @FXML
    protected void browseGitBashExe() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter bashFilter = new ExtensionFilter("Path to bash", "*.*") ;
        fileChooser.getExtensionFilters().add(bashFilter);

        final String pathToGitBashStr = AppConfig.getInstance().getPathToBash();
        if (pathToGitBashStr != null) {
            File bashDir = new File(pathToGitBashStr.substring(0, pathToGitBashStr.lastIndexOf('/')));
            if (bashDir.exists() && bashDir.isDirectory()) {
                fileChooser.setInitialDirectory(bashDir);
            }
        }

        File selectedFile = fileChooser.showOpenDialog(getStage());
        if (selectedFile != null) {
            pathToBash.setText(selectedFile.getAbsolutePath());
        }
        // ELSE: It means that file chooser dialog window was just closed without choosing a file
    }
}
