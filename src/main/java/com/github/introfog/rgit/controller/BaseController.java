package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public abstract class BaseController {
    protected FxmlStageHolder fxmlStageHolder;

    public void initialize(FxmlStageHolder fxmlStageHolder) {
        this.fxmlStageHolder = fxmlStageHolder;
    }

    protected void setClosingOnEscapePressing(FxmlStageHolder fxmlStageHolder) {
        fxmlStageHolder.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fxmlStageHolder.getStage().close();
            }
        });
    }

    protected void closeStage() {
        fxmlStageHolder.getStage().close();
    }

    protected Stage getStage() {
        return fxmlStageHolder.getStage();
    }
}
