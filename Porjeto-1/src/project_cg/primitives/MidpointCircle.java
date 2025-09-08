package project_cg.primitives;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BaseCircle;
import project_cg.primitives.bases.BasePrimitives;

import java.util.function.Consumer;

public class MidpointCircle extends BaseCircle {

    public MidpointCircle(Consumer<Point2D> callback) {
        super(callback);
    }

    public MidpointCircle() {}

    public void drawCircle(int radius) {
        int x = 0;
        int y = radius;
        int d = 1 - radius;

        plotCirclePoints(x, y); // Desenha os pontos a partir da origem

        while (x < y) {
            x++;
            if (d < 0) {
                d += 2 * x + 1;
            } else {
                y--;
                d += 2 * (x - y) + 1;
            }

            plotCirclePoints(x, y);
        }
    }

    // Plota os pontos da circunferência em todos os octantes
    private void plotCirclePoints(int x, int y) {
        callback.accept(new Point2D(x, y));
        callback.accept(new Point2D(-x, y));
        callback.accept(new Point2D(x, -y));
        callback.accept(new Point2D(-x, -y));
        callback.accept(new Point2D(y, x));
        callback.accept(new Point2D(-y, x));
        callback.accept(new Point2D(y, -x));
        callback.accept(new Point2D(-y, -x));
    }

}
