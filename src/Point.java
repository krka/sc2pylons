import java.util.Comparator;
import java.util.Objects;

class Point implements Comparable<Point> {
    private static final Comparator<Point> COMPARATOR = Comparator.comparingInt(Point::getRow).thenComparingInt(Point::getColumn);
    private final int row;
    private final int column;

    public Point(final int row, final int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public int compareTo(final Point o) {
        return COMPARATOR.compare(this, o);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Point rotate(int size) {
        return new Point(size - column - 1, row);
    }

    public Point mirror(int size) {
        return new Point(size - row - 1, column);
    }

    @Override
    public String toString() {
        return "" + '(' + row + ", " + column + ')';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return row == point.row &&
                column == point.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
