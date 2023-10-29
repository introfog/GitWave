package com.github.introfog.rgit.model;

import java.util.prefs.Preferences;

public final class SettingsManager {
    private static final String PATH_TO_REGISTRY = "SOFTWARE\\introfog\\rGit";
    // Computer\HKEY_CURRENT_USER\Software\JavaSoft\Prefs\/S/O/F/T/W/A/R/E//introfog//r/Git
    private static final String PATH_TO_GIT_BASH_EXE = "PATH_TO_GIT_BASH_EXE";

    private SettingsManager() {
        // private constructor
    }

    public static boolean isPathToGitSpecifiedInRegistry() {
        Preferences preferences = Preferences.userRoot().node(PATH_TO_REGISTRY);
        String pathToGitBashExe = preferences.get(PATH_TO_GIT_BASH_EXE, null);
        return pathToGitBashExe != null;
    }

    public static void setPathToGitBashExeInRegistry(String pathToGitBashExe) {
        Preferences preferences = Preferences.userRoot().node(PATH_TO_REGISTRY);
        preferences.put(PATH_TO_GIT_BASH_EXE, pathToGitBashExe);
    }

    public static String getPathToGitBashExeInRegistry() {
        Preferences preferences = Preferences.userRoot().node(PATH_TO_REGISTRY);
        return preferences.get(PATH_TO_GIT_BASH_EXE, null);
    }
}
