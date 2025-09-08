package project_cg.primitives.bases;

import project_cg.geometry.points.Point2D;

import java.util.function.Consumer;

public abstract class BaseCircle extends BasePrimitives {

    public BaseCircle(Consumer<Point2D> callback) {
        super(callback);
    }

    public BaseCircle() {}

    public abstract void drawCircle(int radius);

}
