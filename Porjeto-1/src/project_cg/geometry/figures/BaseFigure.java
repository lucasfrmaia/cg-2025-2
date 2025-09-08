package project_cg.geometry.figures;

import project_cg.geometry.points.Point2D;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseFigure {

    protected List<Point2D> points = new ArrayList<>();

    public abstract String getID();

    public List<Point2D> getPoints() {
        return points;
    }

    public void getVertex(Consumer<Point2D> callback) {
        throw new IllegalArgumentException("Método não implementado");
    }

    public int getColor() {
        return Constants.COLOR_PRIMITEVES;
    }

    public abstract void generatePoints();

    public void iterateToPoints(Consumer<Point2D> callback) {
        for (Point2D point : points) {
            callback.accept(point);
        }
    }
}

