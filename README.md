# JHouseFireExplorer
An easy to use program to find regular expressions in tripcodes such as those
used on the popular 4chan imageboard.

## What works:
* Regular expressions are converted into one RunAutomaton.
* (More or less) random password strings are generated.
* The tripcode of a given string is calculated.
* The RunAutomaton determines whether there is a match.
* The GUI displays matches.

## What is left to do:
* CUDA.
* Optimizing the DES crypt algorithm. It is dreadfully slow.

## How do I compile and run the thing?
You'll want to use ant. Doing so, the everything is very simple:
* "ant compile" compiles the project.
* "ant test" lanches the GUI.
* "ant jar" creates everything you need for a stand-alone run in the dist dir.
* "ant zip" creates housefire.zip in the root, containing the dist dir.
