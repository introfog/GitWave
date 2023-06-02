package com.github.introfog.rgit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
        checkIfGitInstalledAndAvailable();
        searchAndExecuteGitCommand(directory.getText(), gitCommand.getText());
    }

    private static void checkIfGitInstalledAndAvailable() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("git", "--version");

            // Redirect the process output to the console
            processBuilder.inheritIO();

            // Start the process
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            if (line != null && !line.startsWith("git version")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Git isn't available");
                alert.setContentText("Make sure that Git is installed and path to git.exe is in PATH environment variable");
                alert.showAndWait();
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Git command execution completed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void searchAndExecuteGitCommand(String folderPath, String gitCommand) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            searchGitRepositories(folder, gitCommand);
        } else {
            System.out.println("Invalid folder path: " + folderPath);
        }
    }

    private static void searchGitRepositories(File folder, String gitCommand) {
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

    private static boolean isGitRepository(File folder) {
        File gitFolder = new File(folder, ".git");
        return gitFolder.exists() && gitFolder.isDirectory();
    }

    private static void executeGitCommand(File repositoryFolder, String gitCommand) {
        System.out.println("EXECUTE IN " + repositoryFolder.getAbsolutePath());
        String[] commands = gitCommand.split(";");
        for (String command: commands) {
            try {
                // Create a ProcessBuilder for the git command

                ProcessBuilder processBuilder = new ProcessBuilder(command.trim().split("\\s+"));
                processBuilder.directory(repositoryFolder); // Set the repository folder as the working directory

                // Redirect the process output to the console
                processBuilder.inheritIO();

                // Start the process
                Process process = processBuilder.start();

                // Wait for the process to complete
                int exitCode = process.waitFor();
                System.out.println("Git command execution completed with exit code: " + exitCode);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}