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

package com.github.introfog.gitwave.controller.main;

import com.github.introfog.gitwave.controller.SupportController;
import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;

import java.io.File;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryTabController extends SupportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryTabController.class);
    private final TextField directory;

    public DirectoryTabController(FxmlStageHolder fxmlStageHolder, TextField directory) {
        super(fxmlStageHolder);
        this.directory = directory;
    }

    @Override
    public boolean isValid() {
        if (directory.getText().isEmpty()) {
            LOGGER.warn("Directory '{}' is empty, running git command was skipped.", directory.getText());
            DialogFactory.createErrorAlert("Invalid directory", "Directory can't be empty.");
            return false;
        }
        return true;
    }

    public String getDirectory() {
        return directory.getText();
    }

    protected void browseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String lastOpenedFolderPath = AppConfig.getInstance().getLastRunFolder();
        if (lastOpenedFolderPath != null && !lastOpenedFolderPath.isEmpty()) {
            File lastOpenedFolder = new File(lastOpenedFolderPath);
            if (lastOpenedFolder.exists() && lastOpenedFolder.isDirectory()) {
                directoryChooser.setInitialDirectory(lastOpenedFolder);
            }
        }

        File selectedDirectory = directoryChooser.showDialog(getStage());
        if (selectedDirectory != null) {
            directory.setText(selectedDirectory.getAbsolutePath());
        }
    }
}
