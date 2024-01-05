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

import com.github.introfog.gitwave.controller.BaseController;
import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.AppConstants;
import com.github.introfog.gitwave.model.CommandExecutor;
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.ParameterDto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController extends BaseController {
    // TODO add opportunity to check if new release is available for GitWave
    private DirectoryTabController directoryTabController;
    @FXML
    private TextField directory;

    private CommandTabController commandTabController;
    @FXML
    private TextField command;
    @FXML
    private TextField description;
    @FXML
    private Button save;

    private ParametersTabController parametersTabController;
    @FXML
    private Label parametersText;
    @FXML
    private TableView<ParameterDto> parametersTable;

    private SettingsController settingsController;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        final Stage primaryStage = fxmlStageHolder.getStage();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            DialogFactory.createCloseConfirmationAlert(primaryStage);
        });
        directoryTabController = new DirectoryTabController(fxmlStageHolder, directory);
        parametersTabController = new ParametersTabController(fxmlStageHolder, parametersTable, parametersText);
        commandTabController = new CommandTabController(fxmlStageHolder, command, description, save,
                parametersTabController);
        settingsController = new SettingsController(fxmlStageHolder);
    }

    @FXML
    protected void browseDirectory() {
        directoryTabController.browseDirectory();
    }

    @FXML
    protected void clean() {
        commandTabController.clean();
    }

    @FXML
    protected void chooseFromSaved() {
        commandTabController.chooseFromSaved();
    }

    @FXML
    protected void saveCommand() {
        commandTabController.saveCommand();
    }

    @FXML
    protected void runCommand() {
        if (settingsController.isValid() && directoryTabController.isValid()
                && commandTabController.isValid() && parametersTabController.isValid()) {

            AppConfig.getInstance().setLastRunFolder(directoryTabController.getDirectory());

            new Thread(() -> {
                CommandExecutor.searchGitReposAndExecuteCommand(directoryTabController.getDirectory(), command.getText());
            }).start();
        }
    }

    @FXML
    protected void openSettings() {
        settingsController.openSettings();
    }

    @FXML
    protected void openAbout() {
        StageFactory.createModalAboutWindow().getStage().showAndWait();
    }

    @FXML
    protected void foundIssue() {
        AppConfig.getInstance().getHostServices().showDocument(AppConstants.LINK_TO_GIT_CONTRIBUTING_FILE);
    }
}