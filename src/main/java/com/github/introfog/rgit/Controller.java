package com.github.introfog.rgit;

import com.github.introfog.rgit.model.ProcessRunResult;
import com.github.introfog.rgit.model.ProcessRunnerUtil;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Controller {
    @FXML
    private TextField directory;

    @FXML
    private AnchorPane anchor;

    @FXML
    private TextField gitCommand;

    @FXML
    private TextArea output;

    @FXML
    private Button run;

    @FXML
    protected void browseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) anchor.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            directory.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void clearOutput() {
        output.clear();
        output.requestLayout();
    }

    @FXML
    protected void runGitCommand() {
        run.setDisable(true);
        new Thread(() -> {
            searchAndExecuteGitCommand(directory.getText(), gitCommand.getText());
            run.setDisable(false);
        }).start();
    }

    private void searchAndExecuteGitCommand(String folderPath, String gitCommand) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            searchGitRepositories(folder, gitCommand);
        } else {
            output.appendText("Invalid folder path: " + folderPath + "\n");
        }
    }

    private void searchGitRepositories(File folder, String gitCommand) {
        if (isGitRepository(folder)) {
            executeGitCommand(folder, gitCommand);
            return;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Check if the directory is a Git repository
                    if (isGitRepository(file)) {
                        executeGitCommand(file, gitCommand);
                    } else {
                        // Recursively search in subfolders
                        searchGitRepositories(file, gitCommand);
                    }
                }
            }
        }
    }

    private boolean isGitRepository(File folder) {
        File gitFolder = new File(folder, ".git");
        return gitFolder.exists() && gitFolder.isDirectory();
    }

    private void executeGitCommand(File repositoryFolder, String gitCommand) {
        output.appendText("------------EXECUTE IN " + repositoryFolder.getAbsolutePath() + "\n");
        String[] commands = gitCommand.split(";");
        for (String command: commands) {
            try {
                output.appendText("------Run: \"" + command + "\"\n");
                ProcessRunResult result = ProcessRunnerUtil.runProcess(command.trim().split("\\s+"), repositoryFolder);

                output.appendText(result.getErrorLog());
                output.appendText(result.getOutputLog());

                output.appendText("------Exit code: " + result.getExitCode() + "\n");
                output.requestLayout();
            } catch (IOException | InterruptedException e) {
                // TODO write logs into some file and use logger for that purpose
                e.printStackTrace();
            }
        }
    }
}