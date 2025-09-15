package project_cg.primitives;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BaseLine;
import project_cg.primitives.bases.BasePrimitives;

import java.util.function.Consumer;


public class MidpointLine extends BaseLine {

    public MidpointLine(Consumer<Point2D> callback) {
        super(callback);
    }

    public MidpointLine() {}

    @Override
    public void desenhaLinha (Point2D start, Point2D end) {
        int x1 = (int) start.x;
        int y1 = (int) start.y;
        int x2 = (int) end.x;
        int y2 = (int) end.y;

        int dx = x2 - x1;
        int dy = y2 - y1;

        // Caso especial: Linha vertical (dx = 0)
        if (dx == 0) {
            desenhaLinhaVertical(start, end);
            return;
        }

        // Caso especial: Linha horizontal (dy = 0)
        if (dy == 0) {
            desenhaLinhaHorizontal(start, end);
            return;
        }

        dx = Math.abs(x2 - x1);
        dy = Math.abs(y2 - y1);

        int d = 2 * dy - dx;
        int y = y1;

        for (int x = x1; x <= x2; x++) {
            callback.accept(new Point2D(x, y));
            if (d > 0) {
                y++;
                d -= 2 * dx;
            }
            d += 2 * dy;
        }
    }

    // Função para desenhar uma linha vertical usando Point2D
    private void desenhaLinhaVertical(Point2D start, Point2D end) {
        int x = (int) start.x;
        int yStart = (int) Math.min(start.y, end.y);
        int yEnd = (int) Math.max(start.y, end.y);

        for (int y = yStart; y <= yEnd; y++) {
            callback.accept(new Point2D(x, y));
        }
    }

    // Função para desenhar uma linha horizontal usando Point2D
    private void desenhaLinhaHorizontal(Point2D start, Point2D end) {
        int y = (int) start.y;
        int xStart = (int) Math.min(start.x, end.x);
        int xEnd = (int) Math.max(start.x, end.x);

        for (int x = xStart; x <= xEnd; x++) {
            callback.accept(new Point2D(x, y));
        }
    }

}

