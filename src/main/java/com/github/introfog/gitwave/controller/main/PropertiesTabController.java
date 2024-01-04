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

import com.github.introfog.gitwave.controller.SupportController;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.ParameterDto;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class PropertiesTabController extends SupportController {
    private static final Pattern CURL_BRACKETS_PATTERN = Pattern.compile("\\{(\\S+)\\}");
    private final Label parametersText;

    private final TableView<ParameterDto> parametersTable;

    public PropertiesTabController(FxmlStageHolder fxmlStageHolder, TableView<ParameterDto> parametersTable, Label parametersText) {
        super(fxmlStageHolder);
        this.parametersTable = parametersTable;
        this.parametersText = parametersText;
    }

    public void parseCommandParameters(String command) {
        final Set<ParameterDto> parameters = new HashSet<>();
        Matcher matcher = CURL_BRACKETS_PATTERN.matcher(command);
        while (matcher.find()) {
            final String name = matcher.group(1);
            parameters.add(new ParameterDto(name, null));
        }
        if (parameters.isEmpty()) {
            parametersText.setVisible(true);

            parametersTable.setDisable(true);
            parametersTable.setVisible(false);
            parametersTable.getItems().clear();
        } else {
            parametersText.setVisible(false);

            parametersTable.setDisable(false);
            parametersTable.setVisible(true);

            ObservableList<ParameterDto> itemList = FXCollections.observableArrayList(parameters);
            parametersTable.getItems().clear();
            parametersTable.setItems(itemList);

            final TableColumn<ParameterDto, String> nameTableColumn = (TableColumn<ParameterDto, String>) parametersTable.getColumns().get(0);
            nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            nameTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

            final TableColumn<ParameterDto, String> valueTableColumn = (TableColumn<ParameterDto, String>) parametersTable.getColumns().get(1);
            valueTableColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
            valueTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        }
    }
}
