### What is it

Realization of Conway's Game of Life on shaders. Works really fast :)
In addition, there is a mode with Ising model: https://en.wikipedia.org/wiki/Ising_model

### How to run
gradlew desktop:run
more information here: https://github.com/libgdx/libgdx/wiki/Gradle-on-the-Commandline

### Controls

f1-f12: speed of simulation. f1- slowest, f5 - every frame
w : one step forward
space: pause

1,2 - changing algorithm (1 : Game of Life, 2: Ising model)

left mouse click: draw point
right mouse click: draw many points
middle mouse click: move view
scroll: change scale

-,+ : changing temperature (in Ising model only)

### Realization
scala code with libgdx

It was written on 1.4.2016-4.4.2016
