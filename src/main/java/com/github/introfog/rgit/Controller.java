package com.github.introfog.rgit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
    protected void runGitCommand() {
        run.setDisable(true);
        synchronized (output) {
            new Thread(() -> {
                checkIfGitInstalledAndAvailable();
                searchAndExecuteGitCommand(directory.getText(), gitCommand.getText());
                run.setDisable(false);
            }).start();
        }
    }

    private void checkIfGitInstalledAndAvailable() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("git", "--version");

            // Start the process
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            if (line != null && !line.startsWith("git version")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Git not found");
                alert.setContentText("Make sure that Git is installed and path to git.exe is in PATH environment variable and re-run rGit.");
                alert.showAndWait();
            } else {
                output.appendText("Git was found.\n");
            }
            output.appendText("Git command execution completed with exit code: " + exitCode + "\n");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void searchAndExecuteGitCommand(String folderPath, String gitCommand) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            searchGitRepositories(folder, gitCommand);
        } else {
            output.appendText("Invalid folder path: " + folderPath + "\n");
        }
    }

    private  void searchGitRepositories(File folder, String gitCommand) {
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
        output.appendText("EXECUTE IN " + repositoryFolder.getAbsolutePath() + "\n");
        String[] commands = gitCommand.split(";");
        for (String command: commands) {
            try {
                // Create a ProcessBuilder for the git command
                ProcessBuilder processBuilder = new ProcessBuilder(command.trim().split("\\s+"));
                // Set the repository folder as the working directory
                processBuilder.directory(repositoryFolder);

                // Start the process
                Process process = processBuilder.start();
                InputStream inputStream = process.getInputStream();

                // Wait for the process to complete
                int exitCode = process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.appendText(line + "\n");

                }
                output.appendText("Git command execution completed with exit code: " + exitCode + "\n");
                output.requestLayout();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}