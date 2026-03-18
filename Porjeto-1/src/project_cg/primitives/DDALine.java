package project_cg.primitives;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BaseLine;
import project_cg.primitives.bases.BasePrimitives;


import java.util.function.Consumer;

public class DDALine extends BaseLine {

    public DDALine(Consumer<Point2D> callback) {
        super(callback);
    }

    public DDALine() {
    }

    @Override
    public void desenhaLinha(Point2D start, Point2D end) {
        double x0 = start.x;
        double y0 = start.y;
        double xEnd = end.x;
        double yEnd = end.y;

        double dx = xEnd - x0;
        double dy = yEnd - y0;

        double steps;
        double xIncrement, yIncrement;
        double x = x0;
        double y = y0;

        if (Math.abs(dx) > Math.abs(dy)) {
            steps = Math.abs(dx);
        } else {
            steps = Math.abs(dy);
        }

        xIncrement = dx / steps;
        yIncrement = dy / steps;

        callback.accept(new Point2D(Math.round(x), Math.round(y)));

        for (int k = 0; k < steps; k++) {
            x = x + xIncrement;
            y = y + yIncrement;

            callback.accept(new Point2D(Math.round(x), Math.round(y)));
        }
    }

}
