package com.github.introfog.gitwave.controller.main;

import com.github.introfog.gitwave.controller.SupportController;
import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.OsHelper;
import com.github.introfog.gitwave.model.StageFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.UpdateChecker;

import javafx.concurrent.Task;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuController extends SupportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);
    private final Menu menu;
    private final MenuItem updateMenuItem;
    private final SeparatorMenuItem updateMenuItemSeparator;

    public MenuController(FxmlStageHolder fxmlStageHolder, Menu menu, MenuItem updateMenuItem, SeparatorMenuItem updateMenuItemSeparator) {
        super(fxmlStageHolder);
        this.menu = menu;
        this.updateMenuItem = updateMenuItem;
        this.updateMenuItemSeparator = updateMenuItemSeparator;
        new Thread(createCheckForUpdateTask()).start();
    }

    @Override
    public boolean isValid() {
        if (AppConfig.getInstance().getPathToBash() == null) {
            DialogFactory.createErrorAlert("Bash hasn't been specified", "Specify bash in Menu->Settings.");
            return false;
        } if (!OsHelper.isValidPathToBash(AppConfig.getInstance().getPathToBash())) {
            LOGGER.error("Specified path to bash '{}' doesn't point to bash, running git command was skipped.",
                    AppConfig.getInstance().getPathToBash());
            DialogFactory.createErrorAlert("Invalid path to bash", "Specified path \"" + AppConfig.getInstance().getPathToBash() +
                    "\" doesn't point to bash. Specify correct path in settings.", 210);
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

    private Task<Boolean> createCheckForUpdateTask() {
        Task<Boolean> checkForUpdate = new Task<>() {
            @Override
            protected Boolean call() {
                return UpdateChecker.isNewReleaseAvailable();
            }
        };
        checkForUpdate.setOnSucceeded(e -> {
            boolean isNewReleaseAvailable = checkForUpdate.getValue();
            if (isNewReleaseAvailable) {
                this.menu.setText("Menu*");
                this.updateMenuItem.setDisable(false);
                this.updateMenuItem.setVisible(true);
                this.updateMenuItemSeparator.setDisable(false);
                this.updateMenuItemSeparator.setVisible(true);
            }
        });
        return checkForUpdate;
    }
}
