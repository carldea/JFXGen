JFXGen
======

A simple JavaFX game engine.
---------------------------------------------------------------
JFXGen - JavaFX Game Engine

Welcome to a simple JavaFX game engine. This project consists of the core project called jfxgenproj and sub modules or demos. This document will help you show you how to setup your environment and build executables.

Software Requirements:
  - Java 7 SDK or later
  - JavaFX 2.1 SDK or later
  - Gradle Gradle 1.0-rc-1
  - Git

When setting your environment make sure you have 'JAVA_HOME', 'JAVAFX_HOME', and 'GRADLE_HOME'. Please refer to install instructions for your platform (iOS, unix, linux, Windows).

Building the game engine and the demos as Java Webstart applictions.
--------------------------------------------------------------------
mkdir JFXGen
cd JFXGen
git clone git@github.com:carldea/JFXGen.git

gradle -DhostUrl=http://yourhost/path_of_jnlp

Creating a project for IntelliJ IDE:
------------------------------------
cd JFXGen/jfxgenproj

gradle idea

cd JFXGen/demos/atomsmasher

gradle idea

Launch IntelliJ

File -> Open Project 

Navigate (Browse) to the jfxgenproj folder then click 'OK'.
Example:
C:\projects\jfxgen\jfxgenproj

File -> New Module

Import existing module (select radio button)
Select the browse button '...' to locate *.iml file.
Example:
C:\projects\jfxgen\demos\atomsmasher\atomsmasher.iml
Click 'OK'
Click 'Finish'

To run the examples:
--------------------
You can double click the jar file in your file explorer. (On Windows it works)
Provided that you have built the executables you can upload them to your Web server. Make sure that the JNLP file's jnlp XML element having the 'href' attribute contains your location of the jar file (using the -D system property to set the host URL). Another way to run examples in IntelliJ is to Ctrl-Shift-F10 or right click ('Run') the file GameLoopPart2.





