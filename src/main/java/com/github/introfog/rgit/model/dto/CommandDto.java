package com.github.introfog.rgit.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class CommandDto {
    @JsonProperty("command")
    private String command;
    @JsonProperty("comment")
    private String comment;

    public CommandDto(@JsonProperty("command") String command,
            @JsonProperty("comment") String comment) {
        this.command = command;
        this.comment = comment;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommandDto that = (CommandDto) o;
        return Objects.equals(command, that.command) && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, comment);
    }
}
