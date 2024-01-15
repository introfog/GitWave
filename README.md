# GitWave - Multi-Repository Bash Command Runner
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/introfog/GitWave/blob/master/LICENSE.md)

## Overview
Welcome to GitWave – the ultimate tool for developers who work with multiple repositories on Windows. GitWave is 
designed to streamline your workflow by allowing you to run any bash commands across several repositories simultaneously.
It’s a powerful companion for any developer looking to save time and effort when managing their Git projects.

## Features
### 1. Multi-Repository Command Execution
Execute bash commands in multiple repositories with a single command, making it easier to manage complex workflows.
### 2. Command Management
Save your most used or tricky commands with notes, so you never have to remember the syntax again. 
Specify `{parameters}` for the command, to quickly run different scenarios.
### 3. Standalone Application
GitWave is a standalone application that doesn't leave any footprint on your PC. It respects your 
system's cleanliness by ensuring that no unnecessary files or configurations are left behind, 
providing a hassle-free and straightforward experience for users.
### 4. Open Source
GitWave is an open-source project, encouraging collaboration and community involvement. 
Explore, contribute, and customize the application according to your preferences.

## Getting Started
1. Download the latest release from the [releases page](https://github.com/introfog/GitWave/releases).
2. Download and unpack GitWave.zip archive to your local machine. 
3. Launch GitWave.exe and begin executing bash commands across multiple repositories effortlessly.

## Build Locally
### Software Requirements
- JDK 11 or higher.
- Maven.
### Build Options
- To run application execute `mvn javafx:run`.
- To create ready to use app, archived into .zip, run `mvn package`. GitWave will be in `target/GitWave.zip` archive.
  - `GitWave.exe`, which run java image, is located in `tools` folder and generated by separate C++ project `tools/GitWaveExecutor`.

## Ideas For Further Releases
- Allow working with app by using only keyboard (with correct Tabs, Esc and so on work).
- Exclude sub-directories from command running.
- Allow export and import config.
- Release version for Linux and macOS.

## License
GitWave is licensed under the [Apache license](LICENSE.md), providing you with the freedom to use, modify, and distribute the software.

## Contributions
I welcome contributions to my project. If you're interested in helping, please read 
[CONTRIBUTING.md](CONTRIBUTING.md) file for more information on how to get started.


Enjoy the efficiency and cleanliness of GitWave!