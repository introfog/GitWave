# rGit
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/introfog/rGit/blob/master/LICENSE.md)

Welcome to rGit, a Windows desktop application that lets you run recursive git commands and save them with notes. 

rGit is designed for developers who work with multiple git repositories and/or want to save tricky commands with notes.

## Key features
- Allows run any bash commands in multiple repositories by a few clicks.
- Absolutely standalone application, there is no any footprint! All logs, settings, resources and temp files are stored in the app folder.
- There is a command's storage, which you can manage: add, remove, edit commands.
- It is possible to add comments to all your stored commands to not forget what the trickiest ones do.

## How to locally build
### Software requirements
- JDK 11 or higher.
- Maven.
- Any IDE for Java
### Build options
- To run application execute `mvn javafx:run`.
- To create ready to use app, archived into .zip, run `mvn package`. rGit will be in `target/rGitImage/rGit.zip` archive.
  - Windows can consider `rGit.exe` or `batToExe.exe` as a dangerous files and remove them. In that case use `launcher.bat` to run app.

## Ideas for further releases
- Allow working with app by using only keyboard (with correct Tabs, Esc and so on work).
- Exclude sub-directories from command running.
- Implement dynamic fields in commands, e.g. command `git checkout -f {branch}` and you can quickly specify which branch checkout in each command run. 

## Issues and Contributions
If you have any feedback or suggestions, please feel free to open an issue or a pull request on GitHub. Thank you for using rGit!
