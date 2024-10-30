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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OsHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(OsHelper.class);

    private OsHelper() {
        // Do nothing
    }

    public static String getPathToBash(CurrentOs currentOs) {
        switch (currentOs) {
            case WINDOWS:
                LOGGER.info("Find path to git bash for Windows OS.");
                String pathToBash = extractPathToGitForWindows();
                LOGGER.info("Extracted path from Windows Registry: {}", pathToBash);
                if (pathToBash != null) {
                    pathToBash = pathToBash.replace("\\", "/");
                }
                return isValidPathToBash(pathToBash) ? pathToBash : null;
            case LINUX:
            case MACOS:
                return "bash";
            default:
                return null;
        }
    }

    public static boolean isValidPathToBash(String pathToBash) {
        try {
            LOGGER.info("Check if the following path to bash is valid: '{}'", pathToBash);
            if (pathToBash == null || pathToBash.isEmpty()) {
                LOGGER.info("'{}' isn't valid path to bash.", pathToBash);
                return false;
            }
            Process process = Runtime.getRuntime().exec(new String[]{pathToBash, "--version"});

            String line;
            StringBuilder result = new StringBuilder();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = stdout.readLine()) != null) {
                result.append(line);
            }
            stdout.close();

            String output = result.toString();
            final boolean isValid = output.contains("bash");
            LOGGER.info("Output of the command '{} --version', and the result of check for validity is: {}", pathToBash, isValid);
            return isValid;
        } catch (IOException e) {
            LOGGER.warn("Command '{} --version' fail with the following exception.", pathToBash, e);
            return false;
        }
    }

    public static CurrentOs getCurrentOs() {
        CurrentOs currentOs;
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
        return currentOs;
    }

    private static String extractPathToGitForWindows() {
        String installPath = null;

        try {
            String value = readRegistry("HKLM\\SOFTWARE\\GitForWindows", "InstallPath");
            if (value != null && !value.isEmpty()) {
                installPath = value;
            }

            if (installPath == null) {
                value = readRegistry("HKCU\\Software\\GitForWindows", "InstallPath");
                if (value != null && !value.isEmpty()) {
                    installPath = value;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Extracting path from Windows Registry throw an exception.", e);
        }

        return installPath == null ? null : installPath + "\\bin\\bash.exe";
    }

    private static String readRegistry(String location, String key){
        try {
            Process process = Runtime.getRuntime().exec("REG QUERY " + location + " /v " + key);

            String line;
            StringBuilder result = new StringBuilder();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = stdout.readLine()) != null) {
                result.append(line);
            }
            stdout.close();

            // Example of result: HKEY_LOCAL_MACHINE\SOFTWARE\GitForWindows InstallPath REG_SZ E:\Programs\Git
            if( !result.toString().contains(key)){
                return null;
            }

            String[] parsed = result.toString().split("\\s+");
            return parsed[parsed.length-1];
        }
        catch (Exception e) {
            return null;
        }
    }

    public enum CurrentOs {
        WINDOWS,
        MACOS,
        LINUX,
        UNRECOGNISED
    }
}
