package com.github.introfog.rgit.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class ConfigDto {
    @JsonProperty("commands")
    private List<CommandDto> commands;

    public ConfigDto(@JsonProperty("commands") List<CommandDto> commands) {
        this.commands = commands;
    }

    public List<CommandDto> getCommands() {
        return commands;
    }

    public void setCommands(List<CommandDto> commands) {
        this.commands = commands;
    }
}
