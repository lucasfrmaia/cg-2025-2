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
    public void drawEllipse(int Rx, int Ry) {
        // Inicialização das variáveis conforme o algoritmo do slide
        int Rx2 = Rx * Rx;
        int Ry2 = Ry * Ry;
        int twoRx2 = 2 * Rx2;
        int twoRy2 = 2 * Ry2;
        int p;
        int x = 0;
        int y = Ry;
        int px = 0;
        int py = twoRx2 * y;

        /* Plota o ponto inicial em cada quadrante (na origem) */
        plotEllipsePoints(x, y);

        /* --- Região 1 --- */
        p = (int) Math.round(Ry2 - (Rx2 * Ry) + (0.25 * Rx2));
        while (px < py) {
            x++;
            px += twoRy2;
            if (p < 0) {
                p += Ry2 + px;
            } else {
                y--;
                py -= twoRx2;
                p += Ry2 + px - py;
            }
            plotEllipsePoints(x, y);
        }

        /* --- Região 2 --- */
        p = (int) Math.round(Ry2 * (x + 0.5) * (x + 0.5) + Rx2 * (y - 1) * (y - 1) - Rx2 * Ry2);
        while (y > 0) {
            y--;
            py -= twoRx2;
            if (p > 0) {
                p += Rx2 - py;
            } else {
                x++;
                px += twoRy2;
                p += Rx2 - py + px;
            }
            plotEllipsePoints(x, y);
        }
    }

    // Plota os pontos da elipse em todos os quadrantes focados na origem
    private void plotEllipsePoints(int x, int y) {
        callback.accept(new Point2D(x, y));
        callback.accept(new Point2D(-x, y));
        callback.accept(new Point2D(x, -y));
        callback.accept(new Point2D(-x, -y));
    }


}
