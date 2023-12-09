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
import javafx.application.HostServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppConfig {
    private static final String CONFIG_DIR_PATH = "config";
    private static final String CONFIG_FILE_NAME = "config.json";
    private static final String CONFIG_FILE_PATH = CONFIG_DIR_PATH + File.separator + CONFIG_FILE_NAME;
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
    private static final AppConfig INSTANCE = new AppConfig();

    private final ConfigDto config;
    private HostServices hostServices;


    private AppConfig() {
        this.config = AppConfig.initConfig();
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    public HostServices getHostServices() {
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void setPathToGitBashExe(String pathToGitBashExe) {
        config.setPathToGitBash(pathToGitBashExe);
        saveConfig();
    }

    public String getPathToGitBashExe() {
        return config.getPathToGitBash();
    }

    public void setLastRunFolder(String lastRunFolder) {
        config.setLastRunFolder(lastRunFolder);
        saveConfig();
    }

    public String getLastRunFolder() {
        return config.getLastRunFolder();
    }

    public List<CommandDto> getCommands() {
        return config.getCommands();
    }

    public void addCommand(CommandDto commandDto) {
        if (config.getCommands().contains(commandDto)) {
            LOGGER.warn("There was an attempt to add duplicate command '{}'", commandDto);
        } else {
            config.getCommands().add(commandDto);
            saveConfig();
        }
    }

    public void removeCommand(CommandDto commandDto) {
        config.getCommands().remove(commandDto);
        saveConfig();
    }

    public boolean containsCommand(CommandDto commandDto) {
        return config.getCommands().contains(commandDto);
    }

    public void updateCommandScript(CommandDto commandDto, String newCommand) {
        final List<CommandDto> commands = config.getCommands();
        commands.get(commands.indexOf(commandDto)).setCommand(newCommand);
        saveConfig();
    }

    public void updateCommandDescription(CommandDto commandDto, String newDescription) {
        final List<CommandDto> commands = config.getCommands();
        commands.get(commands.indexOf(commandDto)).setDescription(newDescription);
        saveConfig();
    }

    public void updateExistedCommand(CommandDto initial, CommandDto current) {
        final List<CommandDto> commands = config.getCommands();
        if (initial != null && commands.contains(initial)) {
            commands.set(commands.indexOf(initial), current);
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
