# Background

Starcraft 2 is a Real-time strategy game from 2010. Players try to win by constructing production buildings and an army.
The game arena can be - for construction purposes - thought of as two-dimensional grid of discrete cells. Buildings must be placed exactly on these cells. Buildings are either of size 2x2, 3x3 or 4x4 cells.

One of the races called Protoss need to construct their buildings within a power field of another building, called a Pylon (a 2x2 building).

More precisely, a Protoss building may be constructed if the center point of the building is within 6.5 units from the center point of a pylon. You can thus imagine a power field as a circle around the pylon.

![Visualization of a pylon power field](https://liquipedia.net/commons/images/thumb/6/64/Pylons_LotV.jpg/300px-Pylons_LotV.jpg)

# The interesting question

The only 4x4 sized building for Protoss is the Nexus, which does not need a pylon to function. There are however multiple production buildings that are of size 3x3 that do require a pylon.

Naturally, it's interesting to know how many buildings of size 3x3 can be powered by the same pylon.

Let's start by reducing the problem to a purely two-dimensional grid problem.

Since buildings must be constructed on the grid, it's easy to determine which cells can be powered by a pylon.
