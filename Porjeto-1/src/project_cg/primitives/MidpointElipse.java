package project_cg.primitives;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BaseEllipse;
import project_cg.primitives.bases.BasePrimitives;

import java.util.function.Consumer;

public class MidpointElipse extends BaseEllipse {

    public MidpointElipse(Consumer<Point2D> callback) {
        super(callback);
    }

    public MidpointElipse() {}

    // Algoritmo de ponto médio para desenhar a elipse centrada na origem
    @Override
    public void drawEllipse(int a, int b) {
        int x = 0;
        int y = b;
        int d1 = b * b - a * a * b + a * a / 4;
        int dx = 2 * b * b * x;
        int dy = 2 * a * a * y;

        plotEllipsePoints(x, y);

        // Região 1
        while (dx < dy) {
            x++;
            dx += 2 * b * b;
            if (d1 < 0) {
                d1 += dx + b * b;
            } else {
                y--;
                dy -= 2 * a * a;
                d1 += dx - dy + b * b;
            }

            plotEllipsePoints(x, y);
        }

        // Região 2
        double d2 = b * b * (x + 0.5) * (x + 0.5) + a * a * (y - 1) * (y - 1) - a * a * b * b;
        while (y > 0) {
            y--;
            dy -= 2 * a * a;
            if (d2 > 0) {
                d2 += a * a - dy;
            } else {
                x++;
                dx += 2 * b * b;
                d2 += a * a - dy + dx;
            }
            plotEllipsePoints(x, y);
        }
    }

    // Plota os pontos da elipse em todos os octantes sem depender de cx e cy
    private void plotEllipsePoints(int x, int y) {
        callback.accept(new Point2D(x, y));
        callback.accept(new Point2D(-x, y));
        callback.accept(new Point2D(x, -y));
        callback.accept(new Point2D(-x, -y));
    }


}
