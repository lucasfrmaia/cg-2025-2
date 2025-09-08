package project_cg.primitives.bases;

import project_cg.geometry.points.Point2D;

import java.util.function.Consumer;

public abstract class BaseLine extends BasePrimitives {

    public BaseLine(Consumer<Point2D> callback) {
        super(callback);
    }

    public BaseLine() {}

    public abstract void desenhaLinha(Point2D start, Point2D end);

}
