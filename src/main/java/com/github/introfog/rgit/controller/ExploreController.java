package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.model.AlertsUtil;
import com.github.introfog.rgit.model.AppConfig;
import com.github.introfog.rgit.model.StagesUtil;
import com.github.introfog.rgit.model.StagesUtil.FxmlStageHolder;
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
import javafx.stage.Stage;

public class ExploreController {
    @FXML
    private TableView<CommandDto> commandsTable;

    @FXML
    protected Button addNew;

    private ExecuteController executeController;

    public ExploreController() {

    }

    public void setMainController(ExecuteController executeController) {
        this.executeController = executeController;
    }

    @FXML
    protected void commitCommand(CellEditEvent<CommandDto, String> event) {
        event.getRowValue().setCommand(event.getNewValue());
        AppConfig.getInstance().updateCommandScript(event.getRowValue(), event.getNewValue());
    }

    @FXML
    protected void commitComment(CellEditEvent<CommandDto, String> event) {
        event.getRowValue().setComment(event.getNewValue());
        AppConfig.getInstance().updateCommandComment(event.getRowValue(), event.getNewValue());
    }

    @FXML
    protected void removeSelected() {
        CommandDto selectedItem = commandsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            commandsTable.getItems().remove(selectedItem);
            AppConfig.getInstance().removeCommand(selectedItem);
        } else {
            AlertsUtil.createErrorAlert("No row selected", "Please select a row to remove.");
        }
    }

    @FXML
    protected void chooseToRun() {
        CommandDto selectedItem = commandsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            executeController.setGitCommand(selectedItem);
            closeStage();
        } else {
            AlertsUtil.createErrorAlert("No row selected", "Please select a row to run.");
        }
    }

    @FXML
    protected void addNew() {
        FxmlStageHolder holder = StagesUtil.setUpModalStage("view/saver.fxml", "Command saver");

        SaveController saveController = holder.getFxmlLoader().getController();
        saveController.setExploreController(this);

        holder.getStage().showAndWait();
    }

    private void closeStage() {
        Stage modalStage = (Stage) commandsTable.getScene().getWindow();
        modalStage.close();
    }

    public void addNewCommand(CommandDto commandDto) {
        AppConfig.getInstance().addCommand(commandDto);
        commandsTable.getItems().add(commandDto);
    }

    public void fill() {
        List<CommandDto> commandsDtoList = AppConfig.getInstance().getCommands();
        ObservableList<CommandDto> itemList = FXCollections.observableArrayList(commandsDtoList);
        commandsTable.setItems(itemList);

        final TableColumn<CommandDto, String> commandTableColumn = (TableColumn<CommandDto, String>) commandsTable.getColumns().get(0);
        commandTableColumn.setCellValueFactory(new PropertyValueFactory<>("command"));
        commandTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        final TableColumn<CommandDto, String> commentTableColumn = (TableColumn<CommandDto, String>) commandsTable.getColumns().get(1);
        commentTableColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        Platform.runLater(() -> addNew.requestFocus());
    }
}
