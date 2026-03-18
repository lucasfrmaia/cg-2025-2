package project_cg.transformations2d;

import project_cg.geometry.points.Point2D;
import project_cg.transformations.BaseTransformation2d;
import utils.Matrix;

public class Rotation implements BaseTransformation2d {

    private final double angle;

    public Rotation(double angle) {
        this.angle = angle;
    }

    @Override
    public double[][] getTransformation() {
        return getMatrixRotation(angle);
    }

    public static double[][] getMatrixRotation(double angle) {
        double radians = Math.toRadians(angle);

        return new double[][] {
                { Math.cos(radians), -Math.sin(radians), 0 },
                { Math.sin(radians), Math.cos(radians), 0 },
                { 0, 0, 1 }
        };
    }

    @Override
    public String toString() {
        return "R(" + angle + ")";
    }



}
