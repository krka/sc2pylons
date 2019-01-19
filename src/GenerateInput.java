class GenerateInput {
    public static void main(final String[] args) {
        final int size = 14;
        final boolean[][] grid = getGrid(size);
        System.out.println("Number of buildable cells: " + countCells(grid));
        printGrid(size, grid);
    }

    private static int countCells(final boolean[][] grid) {
        int sum = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j]) {
                    sum++;
                }
            }
        }
        return sum;
    }

    private static void printGrid(final int size, final boolean[][] grid) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char c = grid[i][j] ? '#' : ' ';
                if (isCenter(i, size) && isCenter(j, size)) {
                    c = 'P';
                }
                System.out.print(c);
            }
            System.out.println();
        }
    }

    private static boolean isCenter(final int i, final int size) {
        return i == size / 2 || i == size / 2 - 1;
    }

    static boolean[][] getGrid(final int size) {
        final int center = size / 2;
        final double pylonRange2 = 6.5 * 6.5;
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
        // Make sure the pylon itself is marked as unbuildable
        grid[center - 1][center - 1] = false;
        grid[center - 1][center] = false;
        grid[center][center - 1] = false;
        grid[center][center] = false;
        return grid;
    }
}
