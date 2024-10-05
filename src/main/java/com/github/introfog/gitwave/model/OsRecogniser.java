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

package com.github.introfog.gitwave.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OsRecogniser {
    private static final Logger LOGGER = LoggerFactory.getLogger(OsRecogniser.class);

    private static CurrentOs currentOs;

    private OsRecogniser() {
        // Do nothing
    }

    public static boolean isCurrentOsUnixLike() {
        return getCurrentOs() == CurrentOs.MACOS || getCurrentOs() == CurrentOs.LINUX;
    }

    public static CurrentOs getCurrentOs() {
        if (currentOs == null) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                currentOs = CurrentOs.WINDOWS;
            } else if (os.contains("mac")) {
                currentOs = CurrentOs.MACOS;
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                currentOs = CurrentOs.LINUX;
            } else {
                currentOs = CurrentOs.UNRECOGNISED;
            }
            LOGGER.info("System.getProperty(\"os.name\")={}. Recognised OS: {}", os, currentOs);
        }
        return currentOs;
    }

    public enum CurrentOs {
        WINDOWS,
        MACOS,
        LINUX,
        UNRECOGNISED
    }
}
