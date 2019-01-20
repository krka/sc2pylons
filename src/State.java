import java.util.ArrayList;
import java.util.List;

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

    private final ArrayList<State> others;

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
        this.others = new ArrayList<>();
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

    public Placements toPlacements() {
        final List<Point> points = new ArrayList<>();
        addPoints(points);
        return new Placements(grid, points);
    }

    private void addPoints(List<Point> points) {
        for (int column = 0; column < size - 2; column++) {
            if (((1 << column) & combination) != 0) {
                // add the center, not the top left
                points.add(new Point(row + 1, column + 1));
            }
        }
        if (prev != null) {
            prev.addPoints(points);
        }
    }

    @Override
    public String toString() {
        return toPlacements().toString();
    }

    public void addOther(final State other) {
        others.add(other);
    }

    public int countSolutions() {
        int sum = prev == null ? 1 : prev.countSolutions();
        for (final State other : others) {
            sum += other.countSolutions();
        }
        return sum;
    }

    public ArrayList<State> getAllSolutions() {
        final ArrayList<State> solutions = new ArrayList<>();
        for (final State other : others) {
            solutions.addAll(other.getAllSolutions());
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
