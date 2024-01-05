package com.github.introfog.gitwave.controller.main;

import com.github.introfog.gitwave.controller.SupportController;
import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.DialogFactory;
import com.github.introfog.gitwave.model.StageFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsController extends SupportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    public SettingsController(FxmlStageHolder fxmlStageHolder) {
        super(fxmlStageHolder);
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
}
