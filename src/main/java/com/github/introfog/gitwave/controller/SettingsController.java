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
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    @FXML
    private TextField pathToBashExe;

    @FXML
    private Button save;

    @FXML
    private Label done;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        super.setClosingOnEscapePressing(fxmlStageHolder);
        final String pathToGitBashExeStr = AppConfig.getInstance().getPathToGitBashExe();
        if (pathToGitBashExeStr != null && !pathToGitBashExeStr.isEmpty()) {
            pathToBashExe.setText(pathToGitBashExeStr);
        }
        save.requestFocus();
        if (done != null) {
            done.setVisible(false);
        }
    }

    @FXML
    protected void save() {
        File bashExeFile = new File(pathToBashExe.getText());
        if (bashExeFile.exists() && bashExeFile.getAbsolutePath().endsWith(".exe")) {
            final String absolutePath = bashExeFile.getAbsolutePath();
            AppConfig.getInstance().setPathToGitBashExe(absolutePath);
            // TODO #7 add check that path is specified to GitBash.exe not for any other .exe
            LOGGER.info("Path to GitBash.exe registered to '{}'", absolutePath);
            closeStage();
        } else {
            LOGGER.error("Wrong path to GitBash.exe '{}'", bashExeFile.getAbsolutePath());
            DialogFactory.createErrorAlert("Git Bash executable hasn't been specified",
                    "Git Bash executable hasn't been specified correctly. Either specify path manually or find via file browser.", 210);
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
        FileChooser.ExtensionFilter exeFilter = new FileChooser.ExtensionFilter("Bash executable (*.exe)", "*.exe");
        fileChooser.getExtensionFilters().add(exeFilter);

        final String pathToGitBashExeStr = AppConfig.getInstance().getPathToGitBashExe();
        if (pathToGitBashExeStr != null && !pathToGitBashExeStr.isEmpty()) {
            File gitBashDir = new File(pathToGitBashExeStr.substring(0, pathToGitBashExeStr.lastIndexOf("\\")));
            if (gitBashDir.exists() && gitBashDir.isDirectory()) {
                fileChooser.setInitialDirectory(gitBashDir);
            }
        }

        File selectedFile = fileChooser.showOpenDialog(getStage());
        if (selectedFile != null) {
            if (selectedFile.exists()) {
                pathToBashExe.setText(selectedFile.getAbsolutePath());
            } else {
                LOGGER.error("Wrong browsed path to GitBash.exe '{}'", selectedFile);
                DialogFactory.createErrorAlert("Provided file wasn't found", "Provided file wasn't found, try again");
            }
        } else {
            // It means that file chooser dialog window was just closed without choosing a file
        }
    }
}
