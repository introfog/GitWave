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
import com.github.introfog.gitwave.model.StageFactory;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import org.fxmisc.richtext.InlineCssTextArea;

public class ExecutionController extends BaseController {
    @FXML
    private InlineCssTextArea canvas;

    private boolean wasClosed = false;
    private boolean isExecutionFinished = false;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        fxmlStageHolder.getStage().setOnCloseRequest(event -> {
            beforeStageClose();
        });
        canvas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
                if (isExecutionFinished) {
                    closeStage();
                }
            }
        });
        fxmlStageHolder.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
                if (isExecutionFinished) {
                    closeStage();
                }
            }
        });
    }

    public void writeCommand(String command) {
        Platform.runLater(() -> {
            canvas.clear();
        });
        commonWritingPart(command + "\n\n", "-fx-fill: green");
    }

    public void writeDirectory(String path) {
        commonWritingPart("\n" + path + "\n", "-fx-fill: DarkCyan");
    }

    public void writeStandardOutput(String line) {
        commonWritingPart(line + "\n", "-fx-fill: black");
    }

    public void writeErrorOutput(String line) {
        commonWritingPart(line + "\n", "-fx-fill: red");
    }

    public void writeFinishMessage() {
        commonWritingPart("\nPress Enter or Esc to close the window...", "-fx-fill: green");
        isExecutionFinished = true;
    }

    public boolean wasClosed() {
        return wasClosed;
    }

    @Override
    protected void closeStage() {
        beforeStageClose();
        super.closeStage();
    }

    private void commonWritingPart(String line, String style) {
        Platform.runLater(() -> {
            canvas.appendText(line);
            canvas.setStyle(canvas.getLength() - line.length(), canvas.getLength(), style);
            canvas.requestFollowCaret();
        });
    }

    private void beforeStageClose() {
        wasClosed = true;
        StageFactory.unregisterExecutingController(this);
    }
}
