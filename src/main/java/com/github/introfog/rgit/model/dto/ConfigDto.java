package com.github.introfog.rgit.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class ConfigDto {
    @JsonProperty("pathToGitBash")
    private String pathToGitBash;
    @JsonProperty("lastOpenedFolder")
    private String lastOpenedFolder;
    @JsonProperty("commands")
    private List<CommandDto> commands;

    public ConfigDto(
            @JsonProperty("pathToGitBash") String pathToGitBash,
            @JsonProperty("lastOpenedFolder") String lastOpenedFolder,
            @JsonProperty("commands") List<CommandDto> commands) {

        this.pathToGitBash = pathToGitBash;
        this.lastOpenedFolder = lastOpenedFolder;
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

    public String getLastOpenedFolder() {
        return lastOpenedFolder;
    }

    public void setLastOpenedFolder(String lastOpenedFolder) {
        this.lastOpenedFolder = lastOpenedFolder;
    }
}
