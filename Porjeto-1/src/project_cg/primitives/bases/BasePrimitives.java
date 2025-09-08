package project_cg.primitives.bases;

import project_cg.geometry.points.Point2D;

import java.util.function.Consumer;

public abstract class BasePrimitives {

    protected Consumer<Point2D> callback;

    public BasePrimitives(Consumer<Point2D> callback) {
        this.callback = callback;
    }

    public BasePrimitives() {
        this.callback = (point2D -> {
            throw new RuntimeException("CallBack não setada");
        });
    }

    public Consumer<Point2D> getCallback() {
        return callback;
    }

    public void setCallback(Consumer<Point2D> callback) {
        this.callback = callback;
    }

}
