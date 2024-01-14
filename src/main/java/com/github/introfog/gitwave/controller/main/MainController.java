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
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.ParameterDto;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController extends BaseController {
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

    private MenuController menuController;
    @FXML
    private Menu menu;
    @FXML
    private MenuItem updateMenuItem;
    @FXML
    private SeparatorMenuItem updateMenuItemSeparator;

    @FXML
    private ProgressIndicator runProgress;
    @FXML
    private Button run;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        final Stage primaryStage = fxmlStageHolder.getStage();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            if (DialogFactory.createCloseConfirmationAlert() == ButtonType.OK) {
                AppConfig.getInstance().closeApp();
                primaryStage.close();
            };
        });
        directoryTabController = new DirectoryTabController(fxmlStageHolder, directory);
        parametersTabController = new ParametersTabController(fxmlStageHolder, parametersTable, parametersText);
        commandTabController = new CommandTabController(fxmlStageHolder, command, description, save,
                parametersTabController);
        menuController = new MenuController(fxmlStageHolder, menu, updateMenuItem, updateMenuItemSeparator);
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
        // TODO MINOR: it is possible to define all action in code, consider is it worth to do it in that way, or leave @FXML methods
        if (menuController.isValid() && directoryTabController.isValid()
                && commandTabController.isValid() && parametersTabController.isValid()) {

            File directoryToRunIn = new File(directoryTabController.getDirectory());
            if (!directoryToRunIn.exists() || !directoryToRunIn.isDirectory()) {
                DialogFactory.createInfoAlert("Invalid directory",
                        "Specified directory either doesn't exist or isn't a directory.");
                return;
            }
            AppConfig.getInstance().setLastRunFolder(directoryToRunIn.getAbsolutePath());

            new Thread(() -> {
                switchRunButton(true);
                final File scriptFile = CommandExecutor.searchGitRepositoriesAndCreateScriptFile(directoryToRunIn,
                        commandTabController.getCommandWithParameters());
                switchRunButton(false);
                CommandExecutor.executeScriptFileWithCommand(scriptFile);
            }).start();
        }
    }

    @FXML
    protected void openSettings() {
        menuController.openSettings();
    }

    @FXML
    protected void openAbout() {
        menuController.openAbout();
    }

    @FXML
    protected void openUpdate() {
        menuController.openUpdate();
    }

    @FXML
    protected void foundIssue() {
        AppConfig.getInstance().getHostServices().showDocument(AppConstants.LINK_TO_GIT_CONTRIBUTING_FILE);
    }

    private void switchRunButton(boolean inProgress) {
        run.setDisable(inProgress);
        runProgress.setVisible(inProgress);
    }
}