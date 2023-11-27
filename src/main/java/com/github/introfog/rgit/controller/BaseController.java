package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;

import javafx.scene.input.KeyCode;

public abstract class BaseController {
    public abstract void initialize(FxmlStageHolder fxmlStageHolder);

    protected void setClosingOnEscapePressing(FxmlStageHolder fxmlStageHolder) {
        fxmlStageHolder.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fxmlStageHolder.getStage().close();
            }
        });
    }
}
