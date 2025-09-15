package project_cg.primitives;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BaseCircle;
import project_cg.primitives.bases.BasePrimitives;

import java.util.function.Consumer;

public class CircleExplicit extends BaseCircle {

    public CircleExplicit(Consumer<Point2D> callback) {
        super(callback);
    }

    public CircleExplicit() {}

    @Override
    public void drawCircle(int radius) {
        int rSquared = radius * radius;

        // Desenha a partir da origem (0, 0)
        for (int x = 0; x <= radius; x++) {
            int dxSquared = x * x;
            double ySquared = rSquared - dxSquared;

            if (ySquared >= 0) {
                double y = Math.sqrt(ySquared);

                // Desenha em todos os quadrantes do círculo
                callback.accept(new Point2D(x, y));
                callback.accept(new Point2D(x, -y));
                callback.accept(new Point2D(-x, y));
                callback.accept(new Point2D(-x, -y));
            }
        }
    }

}
