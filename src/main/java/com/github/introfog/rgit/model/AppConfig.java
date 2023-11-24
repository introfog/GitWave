package com.github.introfog.rgit.model;

import com.github.introfog.rgit.model.dto.CommandDto;
import com.github.introfog.rgit.model.dto.ConfigDto;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppConfig {
    private static final String CONFIG_DIR_PATH = "config";
    private static final String CONFIG_FILE_NAME = "config.json";

    private static final String CONFIG_FILE_PATH = CONFIG_DIR_PATH + File.separator + CONFIG_FILE_NAME;

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    private final ConfigDto config;

    private static final AppConfig INSTANCE = new AppConfig();

    private AppConfig() {
        this.config = AppConfig.initConfig();
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    public boolean isPathToGitSpecified() {
        return config.getPathToGitBash() != null;
    }

    public void setPathToGitBashExe(String pathToGitBashExe) {
        config.setPathToGitBash(pathToGitBashExe);
        saveConfig();
    }

    public String getPathToGitBashExe() {
        return config.getPathToGitBash();
    }

    public void setLastOpenedFolderInRegistry(String lastOpenedFolder) {
        config.setLastOpenedFolder(lastOpenedFolder);
        saveConfig();
    }

    public String getLastOpenedFolderInRegistry() {
        return config.getLastOpenedFolder();
    }

    public List<CommandDto> getCommands() {
        return config.getCommands();
    }

    public void addCommand(String command, String comment) {
        config.getCommands().add(new CommandDto(command, comment));
        saveConfig();
    }

    public void addCommand(CommandDto commandDto) {
        config.getCommands().add(commandDto);
        saveConfig();
    }

    public void removeCommand(CommandDto commandDto) {
        config.getCommands().remove(commandDto);
        saveConfig();
    }

    public void updateCommandScript(CommandDto commandDto, String newCommand) {
        final List<CommandDto> commands = config.getCommands();
        commands.get(commands.indexOf(commandDto)).setCommand(newCommand);
        saveConfig();
    }

    public void updateCommandComment(CommandDto commandDto, String newComment) {
        final List<CommandDto> commands = config.getCommands();
        commands.get(commands.indexOf(commandDto)).setComment(newComment);
        saveConfig();
    }

    public void updateExistedCommand(CommandDto initial, String command, String comment) {
        // TODO don't allow duplication of CommandDto instances on UI or low levels
        final List<CommandDto> commands = config.getCommands();
        if (initial != null && commands.contains(initial)) {
            final CommandDto newCommand = new CommandDto(command, comment);
            commands.set(commands.indexOf(initial), newCommand);
        }
        saveConfig();
    }

    private void saveConfig() {
        File configDir = new File(CONFIG_DIR_PATH);
        if (!configDir.exists() && !configDir.mkdirs()) {
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

    private static ConfigDto initConfig() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists() && configFile.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                return objectMapper.readValue(configFile, ConfigDto.class);
            } catch (IOException e) {
                LOGGER.error("Failed to read config", e);
                throw new RuntimeException(e);
            }
        } else {
            return new ConfigDto();
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
