import java.util.*;

class Solver {
    private final boolean[][] grid;
    private final int size;
    private final int combinations;

    private Solver(final boolean[][] grid) {
        this.grid = grid;
        size = grid.length;
        combinations = (1 << size);
    }

    public static void main(String[] args) {
        final Solver solver = new Solver(GenerateInput.getGrid(14));
        final State solution = solver.solve();
        System.out.println("Best score: " + solution.getScore());
        System.out.println("Solution:\n" + solution.toString());
    }

    private State solve() {
        HashMap<Integer, State> states = new HashMap<>();
        maybeAddState(states, new State(grid, null, -1, 0, 0, 0));

        for (int row = 0; row < size - 2; row++) {
            states = solveRow(row, states);
        }
        return states.values().stream().max(Comparator.comparing(State::getScore)).get();
    }

    private HashMap<Integer, State> solveRow(int row, HashMap<Integer, State> states) {
        final HashMap<Integer, State> newStates = new HashMap<>();
        for (final State state : states.values()) {
            for (int combination = 0; combination < combinations; combination++) {
                if (isValid(combination)) {
                    final State newState = tryCreate(state, row, combination);
                    if (newState != null) {
                        maybeAddState(newStates, newState);
                    }
                }

            }
        }
        return newStates;
    }

    private State tryCreate(State state, int row, int combination) {
        int numPlacements = 0;
        for (int column = 0; column < size - 2; column++) {
            if ((combination & (1 << column)) != 0) {
                if (!canPlace(row, column, state)) {
                    return null;
                }
                numPlacements++;
            }
        }
        final int bitmask = (state.getBitmask() << 16) | (combination | combination << 1 | combination << 2);
        final int newScore = state.getScore() + numPlacements;
        return new State(grid, state, row, newScore, bitmask, combination);
    }

    private boolean canPlace(int row, int column, State state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!grid[row + i][column + j] || state.filled(row + i, column + j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void maybeAddState(HashMap<Integer, State> states, State state) {
        final State prevState = states.get(state.getBitmask());
        if (prevState == null || state.getScore() > prevState.getScore()) {
            states.put(state.getBitmask(), state);
        }
    }

    private boolean isValid(int combination) {
        return ((combination & (combination >>> 1)) | (combination & (combination >>> 2))) == 0;
    }
}
