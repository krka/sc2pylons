import java.util.*;
import java.util.stream.Collectors;

class Solver {
    private final boolean[][] grid;
    private final int size;
    private final int combinations;

    private Solver(final boolean[][] grid) {
        this.grid = grid;
        size = grid.length;
        combinations = (1 << (size - 2));
    }

    public static void main(final String[] args) {
        final Solver solver = new Solver(GenerateInput.getGrid(14));
        final State solution = solver.solve();
        System.out.println("Best score: " + solution.getScore());
        System.out.println("Number of solutions: " + solution.countSolutions());

        final Map<Integer, List<Placements>> distinct = solution.getAllSolutions().stream()
                .map(State::toPlacements)
                .map(Placements::getCanonical)
                .distinct()
                .collect(Collectors.groupingBy(Placements::symmetries));
        System.out.println("Number of distinct solutions: " + distinct.values().stream().mapToInt(List::size).sum());
        for (int i = 0; i <= 8; i++) {
            final List<Placements> placements = distinct.get(i);
            if (placements != null) {
                System.out.println("Symmetry score " + i + ":");
                final List<List<Placements>> partitions = partition(placements, 10);
                partitions.forEach(solutions -> System.out.println(joinLines(solutions)));
            }
        }
    }

    private static String joinLines(List<Placements> list) {
        List<List<String>> listList = list.stream()
                .map(Placements::toString)
                .map(s -> s.split("\n")).map(Arrays::asList)
                .collect(Collectors.toList());

        final int rows = listList.iterator().next().size();
        int columns = listList.size();

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                List<String> strings = listList.get(j);
                sb.append(strings.get(i));
                if (j == columns - 1) {
                    sb.append("\n");
                } else {
                    sb.append("    ");
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private static <T> List<List<T>> partition(final List<T> list, final int count) {
        final Iterator<T> iterator = list.iterator();
        final List<List<T>> result = new ArrayList<>();
        List<T> current = new ArrayList<>();
        while (iterator.hasNext()) {
            current.add(iterator.next());
            if (current.size() >= count) {
                result.add(current);
                current = new ArrayList<>();
            }
        }
        if (current.size() > 0) {
            result.add(current);
        }
        return result;
    }

    private State solve() {
        HashMap<Integer, State> states = new HashMap<>();
        maybeAddState(states, new State(grid, null, -1, 0, 0, 0, 0));

        for (int row = 0; row < size - 2; row++) {
            states = solveRow(row, states);
        }
        final int bestScore = states.values().stream().max(Comparator.comparing(State::getScore)).get().getScore();
        final List<State> allSolutions = states.values().stream().filter(state -> state.getScore() == bestScore).collect(Collectors.toList());
        final Iterator<State> iterator = allSolutions.iterator();
        final State result = iterator.next();
        while (iterator.hasNext()) {
            result.addOther(iterator.next());
        }
        return result;
    }

    private HashMap<Integer, State> solveRow(final int row, final HashMap<Integer, State> states) {
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

    private State tryCreate(final State state, final int row, final int combination) {
        int numPlacements = 0;
        for (int column = 0; column < size - 2; column++) {
            if ((combination & (1 << column)) != 0) {
                if (!canPlace(row, column, state)) {
                    return null;
                }
                numPlacements++;
            }
        }

        final int newRow = combination | combination << 1 | combination << 2;

        // Filling out starting from row $row, so new bitmask should be $row + 1 and $row + 2
        // $row + 1 is merged from previous $row + 2 and newRow
        // $row + 2 is newRow
        final int newBitmask1 = newRow | state.getBitmask2();

        final int newScore = state.getScore() + numPlacements;
        final State newState = new State(grid, state, row, newScore, newBitmask1, newRow, combination);

        if ((state.getBitmask2() & newRow) != 0) {
            throw new RuntimeException("Assertion failure");
        }

        return newState;
    }

    private boolean canPlace(final int row, final int column, final State state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!grid[row + i][column + j] || state.filled(row + i, column + j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void maybeAddState(final HashMap<Integer, State> states, final State state) {
        final int key = state.getBitmask1() | (state.getBitmask2() << 16);
        final State prevState = states.get(key);
        if (prevState == null) {
            states.put(key, state);
        } else if (state.getScore() > prevState.getScore()) {
            states.put(key, state);
        } else if (state.getScore() == prevState.getScore()) {
            prevState.addOther(state);
        }
    }

    private boolean isValid(final int combination) {
        return ((combination & (combination >>> 1)) | (combination & (combination >>> 2))) == 0;
    }
}
