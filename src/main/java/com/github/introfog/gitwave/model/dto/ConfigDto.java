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

package com.github.introfog.gitwave.model.dto;

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
