### What is it

Realization of Conway's Game of Life on shaders. Works really fast :)

In addition, there is a mode with [Ising model](https://en.wikipedia.org/wiki/Ising_model)

##### How to run
gradlew desktop:run

[more information](https://github.com/libgdx/libgdx/wiki/Gradle-on-the-Commandline)

##### Controls

* f1-f12: speed of simulation. f1 - the slowest, f5 - every frame
* w : one step forward
* space: pause
* 1,2 - changing algorithm (1 : Game of Life, 2: Ising model)
* left mouse click: draw point
* right mouse click: draw many points
* middle mouse click: move view
* scroll: change scale
* -,+ : changing inverse temperature (in Ising model only)

##### Realization
scala code with libgdx library

code placed in core/src

GLSL files in android/assets

If you want to add new mode, just add new java or scala class, which implements CellularAutomaton and modify the next line in Main class:

    private val automats = Array[CellularAutomaton](new GameOfLife(vertexData, textureSize), new IsingModel(vertexData, textureSize))
