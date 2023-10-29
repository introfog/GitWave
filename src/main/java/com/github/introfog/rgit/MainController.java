package com.github.introfog.rgit;

import com.github.introfog.rgit.model.SettingsManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private TextField directory;

    @FXML
    private AnchorPane anchor;

    @FXML
    private TextField gitCommand;

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
        if (directory.getText().isEmpty() || gitCommand.getText().isEmpty()) {
            return;
        }
        run.setDisable(true);
        new Thread(() -> {
            searchAndExecuteGitCommand(directory.getText(), gitCommand.getText());
            run.setDisable(false);
        }).start();
    }

    private void searchAndExecuteGitCommand(String folderPath, String gitCommand) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            List<File> repositoriesToRunCommand = new ArrayList<>();
            searchGitRepositories(folder, repositoriesToRunCommand);
            File scriptFile = null;
            try {
                scriptFile = File.createTempFile("script", ".sh");
                FileWriter writer = new FileWriter(scriptFile);
                writer.write("#!/bin/bash\n");
                writer.write("echo -e \"\\033[0;32m\" \"" + gitCommand + "\" \"\\033[0m\"\n");
                for (File currentFolder : repositoriesToRunCommand) {
                    writer.write("cd " + currentFolder.getAbsolutePath().replace("\\", "\\\\") + "\n");
                    writer.write("echo -e \"\\033[0;36m\" $PWD \"\\033[0m\"\n");

                    writer.write(gitCommand + "\n");
                }
                writer.close();

                String[] str= { "cmd", "/c", "start",
                        SettingsManager.getPathToGitBashExeInRegistry(), "-c",
                        scriptFile.getAbsolutePath().replace("\\", "\\\\\\\\") + ";read -p 'Press Enter to continue...'" };
                Process powerShellProcess = Runtime.getRuntime().exec(str);
                String line;
                System.out.println("Standard Output:");
                BufferedReader stdout = new BufferedReader(new InputStreamReader(
                        powerShellProcess.getInputStream()));
                while ((line = stdout.readLine()) != null) {
                    System.out.println(line);
                }
                stdout.close();
                System.out.println("Standard Error:");
                BufferedReader stderr = new BufferedReader(new InputStreamReader(
                        powerShellProcess.getErrorStream()));
                while ((line = stderr.readLine()) != null) {
                    System.out.println(line);
                }
                stderr.close();
                System.out.println("Done");

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (scriptFile != null && scriptFile.exists()) {
                    scriptFile.delete();
                    // TODO write log if file wasn't deleted
                }
            }
        } else {
            // TODO open error windows
        }
    }

    private void searchGitRepositories(File folder, List<File> repositoriesToRunCommand) {
        if (isGitRepository(folder)) {
            repositoriesToRunCommand.add(folder);
        } else {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (isGitRepository(file)) {
                            repositoriesToRunCommand.add(file);
                        } else {
                            searchGitRepositories(file, repositoriesToRunCommand);
                        }
                    }
                }
            }
        }
    }

    private boolean isGitRepository(File folder) {
        File gitFolder = new File(folder, ".git");
        return gitFolder.exists() && gitFolder.isDirectory();
    }
}