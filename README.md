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