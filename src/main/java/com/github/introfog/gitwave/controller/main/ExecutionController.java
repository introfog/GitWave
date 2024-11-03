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

import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

public class ExecutionController {
    private boolean wasClosed = false;
    private boolean isExecutionFinished = false;
    private final InlineCssTextArea canvas;
    private final Tab tab;
    private final TabPane tabPane;
    private final Map<Tab, ExecutionController> executionTabs;

    public ExecutionController(TabPane tabPane, Map<Tab, ExecutionController> executionTabs) {
        canvas = new InlineCssTextArea("Searching git repositories...");
        canvas.setEditable(false);
        canvas.setFocusTraversable(false);
        canvas.setPadding(new Insets(10, 2, 2, 2));
        canvas.setStyle("-fx-font-family: verdana; -fx-font-size: 12;");

        VirtualizedScrollPane<InlineCssTextArea> scrollPane = new VirtualizedScrollPane<>(canvas);

        tab = new Tab("Execution " + tabPane.getTabs().size());
        tab.setContent(scrollPane);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        canvas.requestFocus();

        this.executionTabs = executionTabs;
        this.tabPane = tabPane;
        executionTabs.put(tab, this);

        tab.setOnCloseRequest(event -> {
            close();
        });
        canvas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
                if (isExecutionFinished) {
                    tabPane.getTabs().remove(tab);
                    close();
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

    public void close() {
        wasClosed = true;
        executionTabs.remove(tab);
        if (!tabPane.getTabs().isEmpty()) {
            Tab previousTab = tabPane.getTabs().get(tabPane.getTabs().size() - 1);
            if (executionTabs.get(previousTab) != null) {
                executionTabs.get(previousTab).canvas.requestFocus();
            }
        }
    }

    private void commonWritingPart(String line, String style) {
        Platform.runLater(() -> {
            canvas.appendText(line);
            canvas.setStyle(canvas.getLength() - line.length(), canvas.getLength(), style);
            canvas.requestFollowCaret();
        });
    }
}
