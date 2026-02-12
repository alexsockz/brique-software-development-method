# BRIQUE GAME by Luis Bolaños Mures (implemented by Alessio Valle and Leonardo Angelotti)

## Introduction:

This repository contains an implementation of the game Brique by Luis Bolaños Mures

The objective was to try and implement this game by following as much as possible the correct methods of software development.

## INSTALL:

In the release page there is the latest stable version, you have 3 options:

### JAR file:

Download the .jar file and double click it to run, otherwise from the CLI write:

java --jar \<PATH-TO-FILE>

this will open the GUI of the game, otherwise you can also plya in CLI by writing:

java --jar \<PATH-TO-FILE> CLI

### ZIP/tar file:

in these archives there will be a bin folder with 2 scripts to run the applications.

### compile the source:

to compile, download the repository and in a terminal, from inside the folder, run:

./gradlew clean build

it will run buld the project that will be now runnable through:

java -cp build/classes/java/main brique.Main CLI


## GENERAL STRUCTURE:

### Packages:

#### brique.core:

contains the worker classes of the game.

#### brique.exceptions:

personalized exceptions to controll different problems during the game loop.

#### brique.rules:

contains the different possible rules, this allows the implementation of different rulesets in a future.

#### brique.ui.cli:

contains 3 separate classes to manage the CLI game, with BriqueCLI being the orchestrator, does not let you choose between game modes but it's easely implementable in the BriqueCLI constructor.

#### brique.ui.gui:

contains all the classes to show the GUI:

MainMenuScreen: too select the gameMode.
gives mode to controller -> controller uses GameEngineFactory to switch between game modes -> obtains correct GameEngine for the game mode.

