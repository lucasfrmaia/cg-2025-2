package project_cg.geometry.clipping;

import project_cg.geometry.points.Point2D;

import java.util.ArrayList;
import java.util.List;

public class SutherlandHodgmanLineClipper {

    private static final double EPSILON = 1e-9;

    private final int xMin;
    private final int yMin;
    private final int xMax;
    private final int yMax;

    public SutherlandHodgmanLineClipper(int xMin, int yMin, int xMax, int yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    public Point2D[] clipLine(int x1, int y1, int x2, int y2) {
        List<Point2D> vertices = new ArrayList<>();
        vertices.add(new Point2D(x1, y1));
        vertices.add(new Point2D(x2, y2));

        vertices = clipAgainstBoundary(vertices, Boundary.LEFT);
        vertices = clipAgainstBoundary(vertices, Boundary.RIGHT);
        vertices = clipAgainstBoundary(vertices, Boundary.BOTTOM);
        vertices = clipAgainstBoundary(vertices, Boundary.TOP);

        if (vertices.size() < 2) {
            return null;
        }

        Point2D first = vertices.getFirst();
        Point2D last = vertices.getLast();

        return new Point2D[] {
                new Point2D(first.x, first.y),
                new Point2D(last.x, last.y)
        };
    }

    private List<Point2D> clipAgainstBoundary(List<Point2D> input, Boundary boundary) {
        if (input.isEmpty()) {
            return input;
        }

        List<Point2D> output = new ArrayList<>();
        Point2D previous = input.getLast();

        for (Point2D current : input) {
            boolean currentInside = isInside(current, boundary);
            boolean previousInside = isInside(previous, boundary);

            if (currentInside) {
                if (!previousInside) {
                    addIfDistinct(output, computeIntersection(previous, current, boundary));
                }
                addIfDistinct(output, current);
            } else if (previousInside) {
                addIfDistinct(output, computeIntersection(previous, current, boundary));
            }

            previous = current;
        }

        return output;
    }

    private boolean isInside(Point2D point, Boundary boundary) {
        return switch (boundary) {
            case LEFT -> point.x >= xMin;
            case RIGHT -> point.x <= xMax;
            case BOTTOM -> point.y >= yMin;
            case TOP -> point.y <= yMax;
        };
    }

    private Point2D computeIntersection(Point2D start, Point2D end, Boundary boundary) {
        double dx = end.x - start.x;
        double dy = end.y - start.y;

        return switch (boundary) {
            case LEFT -> intersectWithVertical(start, dx, dy, xMin);
            case RIGHT -> intersectWithVertical(start, dx, dy, xMax);
            case BOTTOM -> intersectWithHorizontal(start, dx, dy, yMin);
            case TOP -> intersectWithHorizontal(start, dx, dy, yMax);
        };
    }

    private Point2D intersectWithVertical(Point2D start, double dx, double dy, double xBoundary) {
        if (Math.abs(dx) < EPSILON) {
            return new Point2D(xBoundary, start.y);
        }

        double t = (xBoundary - start.x) / dx;
        double y = start.y + (t * dy);
        return new Point2D(xBoundary, y);
    }

    private Point2D intersectWithHorizontal(Point2D start, double dx, double dy, double yBoundary) {
        if (Math.abs(dy) < EPSILON) {
            return new Point2D(start.x, yBoundary);
        }

        double t = (yBoundary - start.y) / dy;
        double x = start.x + (t * dx);
        return new Point2D(x, yBoundary);
    }

    private void addIfDistinct(List<Point2D> points, Point2D candidate) {
        if (points.isEmpty()) {
            points.add(candidate);
            return;
        }

        Point2D last = points.get(points.size() - 1);
        if (Math.abs(last.x - candidate.x) < EPSILON && Math.abs(last.y - candidate.y) < EPSILON) {
            return;
        }

        points.add(candidate);
    }

    private enum Boundary {
        LEFT,
        RIGHT,
        BOTTOM,
        TOP
    }
}
