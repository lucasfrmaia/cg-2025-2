package project_cg.geometry.figures;

import project_cg.primitives.MidpointElipse;
import project_cg.primitives.bases.BaseEllipse;

import java.awt.*;

public class Ellipse extends BaseFigure {

    private int axisA, axisB;

    private BaseEllipse ellipseAlgorithm;

    public Ellipse(int axisA, int axisB, BaseEllipse ellipseAlgorithm) {
        this.axisA = axisA;
        this.axisB = axisB;
        this.ellipseAlgorithm = ellipseAlgorithm;

        generatePoints();
    }

    public void generatePoints() {
        ellipseAlgorithm.setCallback(point2D -> this.points.add(point2D));
        ellipseAlgorithm.drawEllipse(axisA, axisB);
    }

    @Override
    public String getID() {
        return String.format("Elipse [A: %d, B: %d]", axisA, axisB);
    }

    @Override
    public int getColor() {
        return Color.BLUE.getRGB();
    }


}
