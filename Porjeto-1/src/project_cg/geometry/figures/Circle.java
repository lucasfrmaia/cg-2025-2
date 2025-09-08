package project_cg.geometry.figures;

import project_cg.primitives.MidpointCircle;
import project_cg.primitives.bases.BaseCircle;

import java.awt.*;

public class Circle extends BaseFigure {

    private int radius;

    private BaseCircle circleAlgorithm;

    public Circle(int radius, BaseCircle circleAlgorithm) {
        this.radius = radius;
        this.circleAlgorithm = circleAlgorithm;

        generatePoints();
    }

    public void generatePoints() {
        circleAlgorithm.setCallback((point2D -> this.points.add(point2D)));
        circleAlgorithm.drawCircle(radius);
    }

    @Override
    public String getID() {
        return String.format("Circulo [%d]", radius);
    }

    @Override
    public int getColor() {
        return Color.GREEN.getRGB();
    }

}
