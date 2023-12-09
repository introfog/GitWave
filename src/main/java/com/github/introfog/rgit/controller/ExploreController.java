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

package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.DialogFactory;
import com.github.introfog.rgit.model.StageFactory;
import com.github.introfog.rgit.model.StageFactory.FxmlStageHolder;
import com.github.introfog.rgit.model.dto.CommandDto;

import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;

public class ExploreController extends BaseController {
    @FXML
    private TableView<CommandDto> commandsTable;

    @FXML
    protected Button addNew;

    private ExecuteController executeController;

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        super.setClosingOnEscapePressing(fxmlStageHolder);
        fillTable();
        commandsTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fxmlStageHolder.getStage().close();
            }
        });
        Platform.runLater(() -> addNew.requestFocus());
    }

    @FXML
    protected void commitCommand(CellEditEvent<CommandDto, String> event) {
        event.getRowValue().setCommand(event.getNewValue());
        AppConfig.getInstance().updateCommandScript(event.getRowValue(), event.getNewValue());
    }

    @FXML
    protected void commitDescription(CellEditEvent<CommandDto, String> event) {
        event.getRowValue().setDescription(event.getNewValue());
        AppConfig.getInstance().updateCommandDescription(event.getRowValue(), event.getNewValue());
    }

    @FXML
    protected void removeSelectedCommand() {
        CommandDto selectedItem = commandsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            commandsTable.getItems().remove(selectedItem);
            executeController.removeSavedCommand(selectedItem);
            AppConfig.getInstance().removeCommand(selectedItem);
        } else {
            DialogFactory.createErrorAlert("No row selected", "Please select a row to remove.");
        }
    }

    @FXML
    protected void runSelectedCommand() {
        CommandDto selectedItem = commandsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            executeController.setCommand(selectedItem);
            closeStage();
        } else {
            DialogFactory.createErrorAlert("No row selected", "Please select a row to run.");
        }
    }

    @FXML
    protected void addNewCommand() {
        FxmlStageHolder holder = StageFactory.createModalStage("view/saver.fxml", "Command saver");

        SaveController saveController = holder.getFxmlLoader().getController();
        saveController.setExploreController(this);

        holder.getStage().showAndWait();
    }

    void setExecuteController(ExecuteController executeController) {
        this.executeController = executeController;
    }

    void addNewCommand(CommandDto commandDto) {
        AppConfig.getInstance().addCommand(commandDto);
        commandsTable.getItems().add(commandDto);
    }

    private void fillTable() {
        List<CommandDto> commandsDtoList = AppConfig.getInstance().getCommands();
        ObservableList<CommandDto> itemList = FXCollections.observableArrayList(commandsDtoList);
        commandsTable.setItems(itemList);

        final TableColumn<CommandDto, String> commandTableColumn = (TableColumn<CommandDto, String>) commandsTable.getColumns().get(0);
        commandTableColumn.setCellValueFactory(new PropertyValueFactory<>("command"));
        commandTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        final TableColumn<CommandDto, String> descriptionTableColumn = (TableColumn<CommandDto, String>) commandsTable.getColumns().get(1);
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }
}
