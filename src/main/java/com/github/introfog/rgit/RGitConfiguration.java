package com.github.introfog.rgit;

import java.util.prefs.Preferences;

public final class RGitConfiguration {
    private static final String PATH_TO_GIT_BASH_EXE = "path.to.git.bash.exe";

    private static final RGitConfiguration INSTANCE = new RGitConfiguration();

    private final Preferences systemPreferences;

    private RGitConfiguration() {
        this.systemPreferences = Preferences.userNodeForPackage(RGitConfiguration.class);
    }

    public static RGitConfiguration getInstance() {
        return INSTANCE;
    }

    public boolean isPathToGitSpecifiedInRegistry() {
        String pathToGitBashExe = systemPreferences.get(PATH_TO_GIT_BASH_EXE, null);
        return pathToGitBashExe != null;
    }

    public void setPathToGitBashExeInRegistry(String pathToGitBashExe) {
        systemPreferences.put(PATH_TO_GIT_BASH_EXE, pathToGitBashExe);
    }

    public String getPathToGitBashExeInRegistry() {
        return systemPreferences.get(PATH_TO_GIT_BASH_EXE, null);
    }
}
