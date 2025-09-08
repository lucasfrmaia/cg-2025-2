package project_cg.primitives;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BaseCircle;
import project_cg.primitives.bases.BasePrimitives;

import java.util.function.Consumer;

public class TrigometricCircle extends BaseCircle {

    public TrigometricCircle(Consumer<Point2D> callback) {
        super(callback);
    }

    public TrigometricCircle() {}

    public void drawCircle(int radius) {

        for (int angle = 0; angle < 360; angle++) {
            double radians = Math.toRadians(angle);
            double x = (radius * Math.cos(radians));
            double y = (radius * Math.sin(radians));

            callback.accept(new Point2D(x, y));
        }
    }

}
