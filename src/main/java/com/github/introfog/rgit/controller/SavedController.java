package com.github.introfog.rgit.controller;

import com.github.introfog.rgit.RGitConfiguration;
import com.github.introfog.rgit.RGitLauncher;
import com.github.introfog.rgit.model.AlertsUtil;
import com.github.introfog.rgit.model.dto.CommandDto;

import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SavedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SavedController.class);

    @FXML
    private TableView<CommandDto> commandsTable;

    private MainController mainController;

    public SavedController() {

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    protected void commitCommand(CellEditEvent<CommandDto, String> event) {
        event.getRowValue().setCommand(event.getNewValue());
        RGitConfiguration.getInstance().updateCommandScriptFromConfig(event.getRowValue(), event.getNewValue());
    }

    @FXML
    protected void commitComment(CellEditEvent<CommandDto, String> event) {
        event.getRowValue().setComment(event.getNewValue());
        RGitConfiguration.getInstance().updateCommandCommentFromConfig(event.getRowValue(), event.getNewValue());
    }

    @FXML
    protected void removeSelected() {
        CommandDto selectedItem = commandsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            commandsTable.getItems().remove(selectedItem);
            RGitConfiguration.getInstance().removeCommandFromConfig(selectedItem);
        } else {
            AlertsUtil.createErrorAlert("No row selected", "Please select a row to remove.");
        }
    }

    @FXML
    protected void chooseToRun() {
        CommandDto selectedItem = commandsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            mainController.setGitCommand(selectedItem);
            closeStage();
        } else {
            AlertsUtil.createErrorAlert("No row selected", "Please select a row to run.");
        }
    }

    @FXML
    protected void addNew() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader fxmlLoader = new FXMLLoader(RGitLauncher.class.getResource("view/save.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            LOGGER.error("Something goes wrong while opening save command dialog window.", e);
        }

        modalStage.setTitle("rGit save command");
        modalStage.setScene(scene);
        // TODO make design flexible and allow resizing
        modalStage.setResizable(false);

        SaveController saveController = fxmlLoader.getController();
        saveController.setSavedController(this);

        modalStage.showAndWait();

        saveController.setSavedController(null);
    }

    private void closeStage() {
        Stage modalStage = (Stage) commandsTable.getScene().getWindow();
        modalStage.close();
    }

    public void addNewCommand(CommandDto commandDto) {
        RGitConfiguration.getInstance().addCommandToConfig(commandDto);
        commandsTable.getItems().add(commandDto);
    }

    public void fill() {
        List<CommandDto> commandsDtoList = RGitConfiguration.getInstance().getConfig().getCommands();
        ObservableList<CommandDto> itemList = FXCollections.observableArrayList(commandsDtoList);
        commandsTable.setItems(itemList);

        final TableColumn<CommandDto, String> commandTableColumn = (TableColumn<CommandDto, String>) commandsTable.getColumns().get(0);
        commandTableColumn.setCellValueFactory(new PropertyValueFactory<>("command"));
        commandTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        final TableColumn<CommandDto, String> commentTableColumn = (TableColumn<CommandDto, String>) commandsTable.getColumns().get(1);
        commentTableColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }
}
