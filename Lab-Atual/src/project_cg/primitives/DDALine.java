package project_cg.primitives;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BaseLine;
import project_cg.primitives.bases.BasePrimitives;


import java.util.function.Consumer;

public class DDALine extends BaseLine {

    public DDALine(Consumer<Point2D> callback) {
        super(callback);
    }

    public DDALine() {}

    @Override
    public void desenhaLinha(Point2D start, Point2D end) {
        double x1 = start.x;
        double y1 = start.y;
        double x2 = end.x;
        double y2 = end.y;

        double dx = x2 - x1;
        double dy = y2 - y1;

        int steps = (int) Math.max(Math.abs(dx), Math.abs(dy));

        double xIncrement = dx / steps;
        double yIncrement = dy / steps;

        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            callback.accept(new Point2D(x, y));
            x += xIncrement;
            y += yIncrement;
        }
    }
}

