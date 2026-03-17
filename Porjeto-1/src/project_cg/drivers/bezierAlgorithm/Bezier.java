package project_cg.drivers.bezierAlgorithm;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BasePrimitives;

import java.util.List;
import java.util.function.Consumer;

public class Bezier extends BasePrimitives {

    public Bezier(Consumer<Point2D> callback) {
        super(callback);
    }

    public Bezier() {}

    public void drawBezierCurve(List<Point2D> controlPoints, int segments) {
        if (controlPoints == null || controlPoints.size() < 4 || segments <= 0) {
            return;
        }

        Point2D p0 = controlPoints.get(0);
        Point2D p1 = controlPoints.get(1);
        Point2D p2 = controlPoints.get(2);
        Point2D p3 = controlPoints.get(3);

        for (int i = 0; i <= segments; i++) {
            double u = (double) i / segments;

            double b0 = Math.pow(1 - u, 3);
            double b1 = 3 * u * Math.pow(1 - u, 2);
            double b2 = 3 * Math.pow(u, 2) * (1 - u);
            double b3 = Math.pow(u, 3);

            double x = (b0 * p0.x) + (b1 * p1.x) + (b2 * p2.x) + (b3 * p3.x);
            double y = (b0 * p0.y) + (b1 * p1.y) + (b2 * p2.y) + (b3 * p3.y);

            callback.accept(new Point2D(Math.round(x), Math.round(y)));
        }
    }
}