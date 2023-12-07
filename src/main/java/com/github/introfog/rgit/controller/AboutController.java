package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.AppConstants;
import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutController extends BaseController {
    @FXML
    private Label version;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        super.setClosingOnEscapePressing(fxmlStageHolder);
        version.setText(AppConstants.VERSION);
    }

    @FXML
    protected void close() {
        closeStage();
    }

    @FXML
    protected void openGitHub() {
        AppConfig.getInstance().getHostServices().showDocument(AppConstants.LINK_TO_GIT_REPO);
    }
}
