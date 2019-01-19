import java.util.ArrayList;

class State {

    private final boolean[][] grid;
    private final int size;

    private final State prev;
    private final int row;

    // Number of 3x3 buildings placed so far
    private final int score;

    // Bitmask - bit n == 1 means that cell n is filled.
    private final int bitmask1;
    private final int bitmask2;

    // Top left corners of placed buildings
    private final int combination;

    private ArrayList<State> others;

    public State(final boolean[][] grid, final State prev, final int row, final int score, final int bitmask1, final int bitmask2, final int combination) {
        this.grid = grid;
        size = grid.length;
        if (size >= 16) {
            throw new RuntimeException("Too large size");
        }
        this.prev = prev;
        this.row = row;
        this.score = score;
        this.bitmask1 = bitmask1;
        this.bitmask2 = bitmask2;
        this.combination = combination;
    }

    public int getScore() {
        return score;
    }

    public int getBitmask1() {
        return bitmask1;
    }

    public int getBitmask2() {
        return bitmask2;
    }

    public boolean filled(final int row, final int column) {
        final int rowdiff = row - this.row;
        if (rowdiff == 3) {
            return false;
        }
        if (rowdiff == 2) {
            return 0 != ((1 << column) & bitmask2);
        } else  if (rowdiff == 1) {
            return 0 != ((1 << column) & bitmask1);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public String toString() {
        final char[][] output = new char[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                output[row][column] = grid[row][column] ? '#' : '.';
            }
        }
        final int center = size / 2 - 1;
        output[center][center] = '╔';
        output[center][center + 1] = '╗';
        output[center + 1][center] = '╚';
        output[center + 1][center + 1] = '╝';

        makeString(output);
        final StringBuilder stringBuilder = new StringBuilder();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                stringBuilder.append(output[row][column]);
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    private void makeString(final char[][] output) {
        for (int column = 0; column < size - 2; column++) {
            if (((1 << column) & combination) != 0) {
                output[row][column] = '┌';
                output[row][column + 1] = '─';
                output[row][column + 2] = '┐';
                output[row + 1][column] = '│';
                output[row + 1][column + 1] = ' ';
                output[row + 1][column + 2] = '│';
                output[row + 2][column] = '└';
                output[row + 2][column + 1] = '─';
                output[row + 2][column + 2] = '┘';
            }
        }
        if (prev != null) {
            prev.makeString(output);
        }
    }

    public void addOther(final State other) {
        if (others == null) {
            others = new ArrayList<>();
        }
        others.add(other);
    }

    public int countSolutions() {
        int sum;
        if (prev == null) {
            sum = 1;
        } else {
            sum = prev.countSolutions();
        }
        if (others != null) {
            for (State other : others) {
                sum += other.countSolutions();
            }
        }
        return sum;
    }

    public ArrayList<State> getAllSolutions() {
        ArrayList<State> solutions = new ArrayList<>();
        if (others != null) {
            for (State other : others) {
                solutions.addAll(other.getAllSolutions());
            }
        }
        if (prev != null) {
            ArrayList<State> prevSolutions = prev.getAllSolutions();
            for (State prevSolution : prevSolutions) {
                solutions.add(new State(grid, prevSolution, row, score, bitmask1, bitmask2, combination));
            }
        } else {
            solutions.add(this);
        }
        return solutions;
    }
}
