import java.util.*;

public class Placements implements Comparable<Placements> {
    private static final Comparator<Placements> COMPARATOR = Comparator.comparing(Placements::getPoints, Placements::compareLists);
    private final boolean[][] grid;
    private final int size;
    private final List<Point> points;

    public Placements(final boolean[][] grid, final List<Point> points) {
        this.grid = grid;
        this.points = points;
        points.sort(Point::compareTo);
        size = grid.length;
    }

    @Override
    public String toString() {
        final char[][] output = new char[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                output[row][column] = grid[row][column] ? '.' : 'X';
            }
        }
        final int center = size / 2 - 1;
        output[center][center] = '╔';
        output[center][center + 1] = '╗';
        output[center + 1][center] = '╚';
        output[center + 1][center + 1] = '╝';

        points.forEach(point -> {
            final int row = point.getRow() - 1;
            final int column = point.getColumn() - 1;
            output[row][column] = '┌';
            output[row][column + 1] = '─';
            output[row][column + 2] = '┐';
            output[row + 1][column] = '│';
            output[row + 1][column + 1] = ' ';
            output[row + 1][column + 2] = '│';
            output[row + 2][column] = '└';
            output[row + 2][column + 1] = '─';
            output[row + 2][column + 2] = '┘';
        });

        final StringBuilder stringBuilder = new StringBuilder();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                stringBuilder.append(output[row][column]);
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    public Placements getCanonical() {
        final Placements r1 = this.rotate();
        final Placements r2 = r1.rotate();
        final Placements r3 = r2.rotate();
        final Placements m0 = this.mirror();
        final Placements m1 = m0.mirror();
        final Placements m2 = m1.rotate();
        final Placements m3 = m2.rotate();

        final Placements[] all = {this, r1, r2, r3, m0, m1, m2, m3};
        Arrays.sort(all);
        return all[0];
    }

    private Placements rotate() {
        final List<Point> points2 = new ArrayList<>();
        for (final Point point : points) {
            points2.add(point.rotate(size));
        }

        return new Placements(grid, points2);
    }

    private Placements mirror() {
        List<Point> points2 = new ArrayList<>();
        for (Point point : points) {
            points2.add(point.mirror(size));
        }

        return new Placements(grid, points2);
    }

    private List<Point> getPoints() {
        return points;
    }

    @Override
    public int compareTo(final Placements o) {
        return COMPARATOR.compare(this, o);
    }

    private static int compareLists(final List<Point> o1, final List<Point> o2) {
        final Iterator<Point> iterator1 = o1.iterator();
        final Iterator<Point> iterator2 = o2.iterator();
        while (true) {
            if (iterator1.hasNext() && iterator2.hasNext()) {
                final Point p1 = iterator1.next();
                final Point p2 = iterator2.next();
                int c = p1.compareTo(p2);
                if (c != 0) {
                    return c;
                }
            } else if (iterator1.hasNext()) {
                return -1;
            } else if (iterator2.hasNext()) {
                return 1;
            } else {
                return 0;
            }
        }
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Placements that = (Placements) o;
        return grid == that.grid && points.equals(that.points);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(points);
        result = 31 * result + Arrays.hashCode(grid);
        return result;
    }

    public int symmetries() {
        final Placements r1 = this.rotate();
        final Placements r2 = r1.rotate();
        final Placements r3 = r2.rotate();
        final Placements m0 = this.mirror();
        final Placements m1 = m0.mirror();
        final Placements m2 = m1.rotate();
        final Placements m3 = m2.rotate();

        return new HashSet<>(Arrays.asList(this, r1, r2, r3, m0, m1, m2, m3)).size();
    }

}
