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

package com.github.introfog.rgit.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class CommandDto {
    @JsonProperty("command")
    private String command;
    @JsonProperty("description")
    private String description;

    public CommandDto(@JsonProperty("command") String command,
            @JsonProperty("description") String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return Objects.equals(command, that.command) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, description);
    }
}
