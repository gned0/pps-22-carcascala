# CarcaScala - Digital Carcassonne

<p align="center"> 
	<img alt="carcascala logo" src="CarcaScala.png?raw=true" width="500">
</p>

This repository contains a digital version of the popular board game **[Carcassonne](https://en.wikipedia.org/wiki/Carcassonne_(board_game))**, created by **[Klaus-Jürgen Wrede](https://www.kjwrede.de/)** and published by **[Hans im Glück](https://www.hans-im-glueck.de/)**, implemented using the functional programming language **[Scala](https://en.wikipedia.org/wiki/Scala_(programming_language))**, including small features implemented via **[Prolog](https://en.wikipedia.org/wiki/Prolog)**, a logic programming language, for the **PPS** course (**Paradigmi di Programmazione e Sviluppo**). <br>

### What the project is

The project implements the original gameplay loop and looks of the original board game release, that is a turn-based game where each turn a different player draws a tile from a randomized deck, decides whether to place the tile on the board or not, by pairing the borders of tile that has been drawn, and what the borders available on the board are, and after that, the player can again decide whether to place a follower on the features of the placed tile or not, in order to gain a certain amount of points based on the rules dictated by the feature onto which the follower has been placed on. <br>
The whole game, aside from a couple of features, has been implemented using Scala, in order to maximize the use of the functional programming paradigm when developing the project. A few features of the game have been developed using Prolog instead of Scala, in order to show off the capabilities of logic programming. The features developed via Prolog are related to the calculation of the players scores.  

### What is implemented

The implemented digital version is based off of the main release of the board game with its rules, as of now no expansions have been currently implemented, more technical details on the game itself can be found in the previously linked Wikipedia page of the board game.

### Play the game
In order to play the game, one can download the available jar in the release tab, but requires Java SE 17 or newer. Generally speaking in order to run the game the steps are as follows:
 1. Download and install [Java SE 17](https://www.oracle.com/de/java/technologies/javase-downloads.html) or a newer version, based on your OS of preference.
 2. Download and run the [latest executable]() either by double clicking or using the appropriate commands to run a jar file on a command line/prompt.
