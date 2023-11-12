package com.github.introfog.rgit;

import com.github.introfog.rgit.model.dto.CommandDto;
import com.github.introfog.rgit.model.dto.ConfigDto;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RGitConfiguration {
    private static final String PATH_TO_GIT_BASH_EXE = "path.to.git.bash.exe";

    private static final String LAST_OPENED_FOLDER = "last.opened.folder";

    private static final String CONFIG_DIR_PATH = "config";
    private static final String CONFIG_FILE_NAME = "config.json";

    private static final String CONFIG_FILE_PATH = CONFIG_DIR_PATH + File.separator + CONFIG_FILE_NAME;

    private static final Logger LOGGER = LoggerFactory.getLogger(RGitConfiguration.class);


    private ConfigDto config;

    private static final RGitConfiguration INSTANCE = new RGitConfiguration();

    private final Preferences systemPreferences;

    private RGitConfiguration() {
        this.systemPreferences = Preferences.userNodeForPackage(RGitConfiguration.class);
    }

    public static RGitConfiguration getInstance() {
        return INSTANCE;
    }

    public boolean isPathToGitSpecifiedInRegistry() {
        String pathToGitBashExe = systemPreferences.get(PATH_TO_GIT_BASH_EXE, null);
        return pathToGitBashExe != null;
    }

    public void setPathToGitBashExeInRegistry(String pathToGitBashExe) {
        systemPreferences.put(PATH_TO_GIT_BASH_EXE, pathToGitBashExe);
    }

    public String getPathToGitBashExeInRegistry() {
        return systemPreferences.get(PATH_TO_GIT_BASH_EXE, null);
    }

    public void setLastOpenedFolderInRegistry(String pathToGitBashExe) {
        systemPreferences.put(LAST_OPENED_FOLDER, pathToGitBashExe);
    }

    public String getLastOpenedFolderInRegistry() {
        return systemPreferences.get(LAST_OPENED_FOLDER, null);
    }

    public void initConfig() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists() && configFile.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                config = objectMapper.readValue(configFile, ConfigDto.class);
            } catch (IOException e) {
                LOGGER.error("Failed to read config", e);
                throw new RuntimeException(e);
            }
        } else {
            config = new ConfigDto(new ArrayList<>());
        }
    }

    public ConfigDto getConfig() {
        return config;
    }

    public void addCommandToConfig(String command, String comment) {
        config.getCommands().add(new CommandDto(command, comment));
        saveConfig();
    }

    public void saveConfig() {
        File configDir = new File(CONFIG_DIR_PATH);
        if (!configDir.mkdirs()) {
            LOGGER.error("Failed to create the directory '{}'", CONFIG_DIR_PATH);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        try {
            final File configFilePath = new File(CONFIG_FILE_PATH);
            objectMapper.writer(new CustomPrettyPrinter()).writeValue(configFilePath, config);
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
            throw new RuntimeException(e);
        }
    }

    private static class CustomPrettyPrinter extends DefaultPrettyPrinter {
        public CustomPrettyPrinter() {
            _objectFieldValueSeparatorWithSpaces = ": ";
            indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withLinefeed("\n"));
            indentObjectsWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withLinefeed("\n"));
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new CustomPrettyPrinter();
        }
    }
}
