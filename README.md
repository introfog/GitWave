# rGit
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/introfog/rGit/blob/master/LICENSE.md)

Welcome to rGit, a Windows desktop application that lets you run recursive git commands and save them with notes. 

rGit is designed for developers who work with multiple git repositories and/or want to save tricky commands with notes.

## How to locally build
### Software requirements
- JDK 11 or higher.
- To create rGit installer:
  - Download and install [Inno Setup 6 or higher](https://jrsoftware.org/isinfo.php).
  - Add an `Inno Setup` folder to `Path` environment variable.
### Build options
- To run application execute `mvn javafx:run`.
- To create local ready to use Java image execute `mvn package`. rGit will be in `target/rGitImage` folder.
- To create installer of app execute `mvn package -P create-installer`. Installer of rGit will be in `installer` folder.

## Issues and Contributions
If you have any feedback or suggestions, please feel free to open an issue or a pull request on GitHub. Thank you for using rGit!
