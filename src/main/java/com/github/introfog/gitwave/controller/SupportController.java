package com.github.introfog.gitwave.controller;

import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;

public abstract class SupportController extends BaseController {
    public SupportController(FxmlStageHolder fxmlStageHolder) {
        initialize(fxmlStageHolder);
    }
}
