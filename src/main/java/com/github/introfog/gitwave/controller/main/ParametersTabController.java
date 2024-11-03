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
import com.github.introfog.gitwave.model.DialogFactory;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParametersTabController extends SupportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParametersTabController.class);
    private static final Pattern PARAMETERS_PATTERN = Pattern.compile("\\{([^{}\\s]+)\\}");
    private static final Pattern ONLY_SPACES_PATTERN = Pattern.compile("^\\s*$");
    private final Label parametersText;
    private final TableView<ParameterDto> parametersTable;
    private final Set<ParameterDto> parameters = new HashSet<>();

    public ParametersTabController(FxmlStageHolder fxmlStageHolder, TableView<ParameterDto> parametersTable, Label parametersText) {
        super(fxmlStageHolder);
        this.parametersTable = parametersTable;
        this.parametersText = parametersText;
    }

    @Override
    public boolean isValid() {
        for (ParameterDto parameter : parameters) {
            final String value = parameter.getValue();
            final String name = parameter.getName();
            if (value == null || ONLY_SPACES_PATTERN.matcher(value).matches()) {
                LOGGER.warn("Parameter '{}' hasn't been specified yet.", name);
                DialogFactory.createErrorAlert("Invalid parameter",
                        "Parameter {" + name + "} hasn't been specified yet, either remove or set a non-empty value.", 210);
                return false;
            }
        }
        return true;
    }

    public String applyParameters(String command) {
        for (ParameterDto param : parameters) {
            final String name = param.getName();
            final String value = param.getValue();
            command = command.replaceAll("\\{" + name + "\\}", value);
        }
        return command;
    }

    public void parseCommandParameters(String command) {
        parameters.clear();
        Matcher matcher = PARAMETERS_PATTERN.matcher(command);
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

            valueTableColumn.setOnEditCommit(event -> {
                ParameterDto editedDto = event.getRowValue();
                ParameterDto found = parameters.stream().filter(item -> item.equals(editedDto)).findFirst().orElse(null);
                if (found == null) {
                    LOGGER.error("While updating parameters table, parameter '{}' wasn't found.", editedDto);
                } else {
                    found.setValue(event.getNewValue());
                }
            });
        }
    }
}
