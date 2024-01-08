package com.github.introfog.gitwave.controller.main;

import com.github.introfog.gitwave.controller.SupportController;
import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.UpdateChecker;

import java.io.File;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuController extends SupportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);
    private Menu menu;
    private MenuItem updateMenuItem;
    private SeparatorMenuItem updateMenuItemSeparator;

    public MenuController(FxmlStageHolder fxmlStageHolder, Menu menu, MenuItem updateMenuItem, SeparatorMenuItem updateMenuItemSeparator) {
        super(fxmlStageHolder);
        this.menu = menu;
        this.updateMenuItem = updateMenuItem;
        this.updateMenuItemSeparator = updateMenuItemSeparator;
        if (UpdateChecker.isNewReleaseAvailable()) {
            this.menu.setText("Menu*");
            this.updateMenuItem.setDisable(false);
            this.updateMenuItem.setVisible(true);
            this.updateMenuItemSeparator.setDisable(false);
            this.updateMenuItemSeparator.setVisible(true);
        }
    }

    @Override
    public boolean isValid() {
        final String pathToGitBashExe = AppConfig.getInstance().getPathToGitBashExe();
        if (pathToGitBashExe == null || pathToGitBashExe.isEmpty()) {
            StageFactory.createModalSettingsWindow().getStage().showAndWait();
            return false;
        } if (!(new File(pathToGitBashExe)).exists()) {
            LOGGER.error("Specified GitBash.exe path '{}' points to not-existent file, running git command was skipped.", pathToGitBashExe);
            DialogFactory.createErrorAlert("Invalid path to GitBash.exe", "Specified path \"" + pathToGitBashExe +
                    "\" points to not-existent file. Specify correct path in settings.");
            return false;
        }
        return true;
    }

    public void openSettings() {
        StageFactory.createModalSettingsWindow().getStage().showAndWait();
    }

    public void openAbout() {
        StageFactory.createModalAboutWindow().getStage().showAndWait();
    }

    public void openUpdate() {
        StageFactory.createModalUpdateWindow().getStage().showAndWait();
    }
}
