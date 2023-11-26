package com.github.introfog.rgit.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class ConfigDto {
    @JsonProperty("pathToGitBash")
    private String pathToGitBash;
    @JsonProperty("lastOpenedFolder")
    private String lastRunFolder;
    @JsonProperty("commands")
    private List<CommandDto> commands;

    public ConfigDto(
            @JsonProperty("pathToGitBash") String pathToGitBash,
            @JsonProperty("lastOpenedFolder") String lastRunFolder,
            @JsonProperty("commands") List<CommandDto> commands) {

        this.pathToGitBash = pathToGitBash;
        this.lastRunFolder = lastRunFolder;
        this.commands = commands;
    }

    public ConfigDto() {
        this(null, null, new ArrayList<>());
    }

    public List<CommandDto> getCommands() {
        return commands;
    }

    public void setCommands(List<CommandDto> commands) {
        this.commands = commands;
    }

    public String getPathToGitBash() {
        return pathToGitBash;
    }

    public void setPathToGitBash(String pathToGitBash) {
        this.pathToGitBash = pathToGitBash;
    }

    public String getLastRunFolder() {
        return lastRunFolder;
    }

    public void setLastRunFolder(String lastOpenedFolder) {
        this.lastRunFolder = lastOpenedFolder;
    }
}
