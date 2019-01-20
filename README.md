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

Since buildings must be constructed on the grid, it's easy to determine which cells can be powered by a pylon:
```java
        final double pylonRange2 = 6.5 * 6.5;
        final int size = 14;
        final int center = size / 2;
        final boolean[][] grid = new boolean[size][size];
        for (int i = 1; i < size - 1; i++) {
            for (int j = 1; j < size - 1; j++) {
                double dx = i - center + 0.5;
                double dy = j - center + 0.5;
                double d2 = dx * dx + dy * dy;
                if (d2 <= pylonRange2) {
                    for (int k = 0; k < 3; k++) {
                        for (int l = 0; l < 3; l++) {
                            grid[i + k - 1][j + l - 1] = true;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String c = grid[i][j] ? "." : "X";
                if ((i == center || i == center - 1) && (j == center || j == center - 1)) {
                    c = "P";
                }
                System.out.print(c);
            }
            System.out.println();
        }
```

We then get this output:
```
XXX........XXX
X............X
X............X
..............
..............
..............
......PP......
......PP......
..............
..............
..............
X............X
X............X
XXX........XXX```
where `X` means out of range, `.` is a buildable cell, `P` is where pylon itself is positioned.

To get an upper bound of the number of 3x3 buildings we can place on this grid, we take the number of `#` cells and divide by `3*3`: `172 / 9 = 19.111...`

It's therefore not possible to place more than 19 3x3 buildings for a single pylon.

# Current knowledge

This answer has been asked and answered before, see
[stackexchange/whats-the-maximum-number-of-gateways-you-can-power-off-1-pylon](https://gaming.stackexchange.com/questions/135803/whats-the-maximum-number-of-gateways-you-can-power-off-1-pylon)
and
[/r/16_gates_1_pylon_in_response_to_rotterdams](https://www.reddit.com/r/starcraft/comments/1ok9uv/16_gates_1_pylon_in_response_to_rotterdams/)

but I am not aware if any further optimizations have been properly investigated.

Here follows an attempt to do just that.

# Finding an exact solution

How do we find an optimal solution to this? We could obviously try all combinations of placements for 19 buildings until we find something that works.
We have 172 buildable cells, and 108 possible 3x3 building placements, so that would mean we need to try `108 * 107 * .. * 90` combinations - far too many to do efficiently.

A better approach would be to do it with Dynamic Programming. We evaluate one row at a time and keep track of how many buildings
we have placed so far, as well as a bitmap of which cells we have filled in the two rows above. If we encounter the same bitmap twice, we keep the one with the highest number of buildings.

This is somewhat feasible approach. For each row we visit, we'll need to evaluate all combinations of placements on that row, which is on the order of `2^N`
(but our N is 14 so it's ok). For each such combination we also need to compare against our bitmap. The bitmap is of size `2*N` so there could be up to `2^(2*N)` of them.
For each row we get a total runtime of `2^(3N)`. Fortunately since we're only placing 3x3 buildings on the grid, most of the combinations can be filtered out so it's less than that in practice.

We then need to do this for each row, so we end up with: `N*2^(3N)`.

This can be implemented efficiently and we get our answer.

You can not place more than 16 3x3-buildings in a single pylon field.
You can do that in 834 different ways.
255 of those are unique when you take rotations and mirroring into account.

Here's the actual output:

```
Best score: 16
Number of solutions: 834
Number of distinct solutions: 255
Symmetry score 2:
XXX.┌─┐┌─┐.XXX    XXX.┌─┐┌─┐.XXX    XXX.┌─┐┌─┐.XXX    XXX.┌─┐┌─┐.XXX    XXX.┌─┐....XXX
X┌─┐│ ││ │┌─┐X    X┌─┐│ ││ │┌─┐X    X┌─┐│ ││ │┌─┐X    X┌─┐│ ││ │┌─┐X    X┌─┐│ │┌─┐┌─┐X
X│ │└─┘└─┘│ │X    X│ │└─┘└─┘│ │X    X│ │└─┘└─┘│ │X    X│ │└─┘└─┘│ │X    X│ │└─┘│ ││ │X
.└─┘┌─┐...└─┘.    .└─┘.┌─┐..└─┘.    .└─┘┌─┐┌─┐└─┘.    .└─┘┌─┐┌─┐└─┘.    .└─┘┌─┐└─┘└─┘.
┌─┐.│ │.┌─┐┌─┐    ┌─┐..│ │...┌─┐    ┌─┐.│ ││ │.┌─┐    .┌─┐│ ││ │┌─┐.    .┌─┐│ │.┌─┐┌─┐
│ │.└─┘.│ ││ │    │ │..└─┘┌─┐│ │    │ │.└─┘└─┘.│ │    .│ │└─┘└─┘│ │.    .│ │└─┘.│ ││ │
└─┘...╔╗└─┘└─┘    └─┘┌─┐╔╗│ │└─┘    └─┘...╔╗...└─┘    .└─┘..╔╗..└─┘.    .└─┘..╔╗└─┘└─┘
┌─┐┌─┐╚╝...┌─┐    ┌─┐│ │╚╝└─┘┌─┐    ┌─┐...╚╝...┌─┐    .┌─┐..╚╝..┌─┐.    ┌─┐┌─┐╚╝..┌─┐.
│ ││ │.┌─┐.│ │    │ │└─┘┌─┐..│ │    │ │.┌─┐┌─┐.│ │    .│ │┌─┐┌─┐│ │.    │ ││ │.┌─┐│ │.
└─┘└─┘.│ │.└─┘    └─┘...│ │..└─┘    └─┘.│ ││ │.└─┘    .└─┘│ ││ │└─┘.    └─┘└─┘.│ │└─┘.
.┌─┐...└─┘┌─┐.    .┌─┐..└─┘.┌─┐.    .┌─┐└─┘└─┘┌─┐.    .┌─┐└─┘└─┘┌─┐.    .┌─┐┌─┐└─┘┌─┐.
X│ │┌─┐┌─┐│ │X    X│ │┌─┐┌─┐│ │X    X│ │┌─┐┌─┐│ │X    X│ │┌─┐┌─┐│ │X    X│ ││ │┌─┐│ │X
X└─┘│ ││ │└─┘X    X└─┘│ ││ │└─┘X    X└─┘│ ││ │└─┘X    X└─┘│ ││ │└─┘X    X└─┘└─┘│ │└─┘X
XXX.└─┘└─┘.XXX    XXX.└─┘└─┘.XXX    XXX.└─┘└─┘.XXX    XXX.└─┘└─┘.XXX    XXX....└─┘.XXX
```

(For full output, run the program)

# The code

All of the code for computing this is inside this repository.
