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

package com.github.introfog.gitwave.controller;

import com.github.introfog.gitwave.model.AppConfig;
import com.github.introfog.gitwave.model.StageFactory.FxmlStageHolder;
import com.github.introfog.gitwave.model.dto.CommandDto;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class ExploreController extends BaseController {
    @FXML
    private TableView<CommandDto> commandsTable;

    private CommandDto pickedItem;
    private final List<CommandDto> removedItems = new ArrayList<>();

    @Override
    public void initialize(FxmlStageHolder fxmlStageHolder) {
        super.initialize(fxmlStageHolder);
        super.setClosingOnEscapePressing(fxmlStageHolder);
        setUpAndFillTable();
        commandsTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fxmlStageHolder.getStage().close();
            }
        });
    }

    @FXML
    protected void mouseClick(MouseEvent event) {
        if (event.getClickCount() >= 2) {
            final CommandDto selectedItem = commandsTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                pickedItem = selectedItem;
                closeStage();
            }
        }
    }

    public CommandDto getPickedItem() {
        return pickedItem;
    }

    public List<CommandDto> getRemovedItems() {
        return removedItems;
    }

    private void setUpAndFillTable() {
        List<CommandDto> commandsDtoList = AppConfig.getInstance().getCommands();
        ObservableList<CommandDto> itemList = FXCollections.observableArrayList(commandsDtoList);
        commandsTable.setItems(itemList);

        final TableColumn<CommandDto, String> commandTableColumn = (TableColumn<CommandDto, String>) commandsTable.getColumns().get(0);
        commandTableColumn.setCellValueFactory(new PropertyValueFactory<>("command"));
        commandTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        final TableColumn<CommandDto, String> descriptionTableColumn = (TableColumn<CommandDto, String>) commandsTable.getColumns().get(1);
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<CommandDto, String> removeTableColumn = (TableColumn<CommandDto, String>) commandsTable.getColumns().get(2);
        removeTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        removeTableColumn.setCellFactory(column -> new RemoveTableCell(this));
    }

    private static class RemoveTableCell extends TableCell<CommandDto, String> {
        private final Button removeButton = new Button("X");

        private final ExploreController exploreController;

        public RemoveTableCell(ExploreController exploreController) {
            this.exploreController = exploreController;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(removeButton);
                removeButton.setOnAction(event -> {
                    CommandDto selectedItem = getTableView().getItems().get(getIndex());
                    if (selectedItem != null) {
                        exploreController.commandsTable.getItems().remove(selectedItem);
                        exploreController.removedItems.add(selectedItem);
                    }
                });
            }
        }
    }
}
