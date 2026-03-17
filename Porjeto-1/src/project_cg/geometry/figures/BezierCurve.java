package project_cg.geometry.figures;

import project_cg.drivers.bezierAlgorithm.Bezier;
import project_cg.geometry.points.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BezierCurve extends BaseFigure {

    private final List<Point2D> controlPoints;
    private final int segments;
    private final Bezier bezierAlgorithm;

    public BezierCurve(List<Point2D> controlPoints, int segments, Bezier bezierAlgorithm) {
        this.controlPoints = new ArrayList<>(controlPoints);
        this.segments = segments;
        this.bezierAlgorithm = bezierAlgorithm;

        generatePoints();
    }

    @Override
    public void generatePoints() {
        bezierAlgorithm.setCallback(point2D -> this.points.add(point2D));
        bezierAlgorithm.drawBezierCurve(controlPoints, segments);
    }

    @Override
    public String getID() {
        return String.format("Bezier Cúbica [seg: %d]", segments);
    }

    @Override
    public int getColor() {
        return Color.ORANGE.getRGB();
    }
}