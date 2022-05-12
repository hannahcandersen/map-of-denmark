Source for template: https://gist.github.com/PurpleBooth/109311bb0361f32d87a2



# First-year Project: Map of Denmark (Spring 2022)
Map of Denmark is a system for visualizing and working with map data in the OpenStreetMap .OSM format.

The purpose of the system is to visualize the map data and make it possible for a user to search for the
shortest route between two addresses. 


## Description
This is our final solution of the First-year Project.
The map can load and draw only smaller parts of Denmark e.g. Bornholm, Fyn and Amager.
The runnable program has different functionalities including route-finding via address search, showing
transport methods (car/bicycle/walking), showing nearest neighbour, a graphical scalebar in meters, ability to choose
a different data file and the to change the map appearance (debug mode/party mode/graph mode).


## Getting Started
Open the project location in the terminal. Make sure you are located in the path */BFST22Group25 and run the command:

on MacOS
```
./gradlew run
```
on Windows
```
.\gradlew run
```


## Useful information for users
HOW TO SEARCH FOR AN ADDRESS
- It is only possible to search via the name of the street
- It has to be written exactly as spelled
  - e.g 'Rued Langgaardsvej' has to start with capital letter and include space
- Suggested addresses will appear after the fourth letter has been entered

TRANSPORT TYPES
- Route-finding by car is measured by time
- Route-finding by bicycle and walking is measured by distance

NEAREST NEIGHBOUR
- Double click to show nearest neighbour on the map

DEBUG MODE
- Debug mode is only for developers


## Versioning
We have used [github.itu](https://github.itu.dk/trbj/BFST22Group25) for versioning.
The final release can be found: [Releases](https://github.itu.dk/trbj/BFST22Group25/releases)


## Authors
* **Hannah Camilla Andersen** - *hcan@itu.dk*
* **Katrine Meyer** - *kmey@itu.dk*
* **Nikolaj Mølbak Troest** - *ntro@itu.dk*
* **Turið Højgaard** - *tuho@itu.dk*


## Acknowledgments
* TA's from studyLab
* We would not have made it through this project without our good TA's, Nadja, and the great Emil from StudyLab :)

