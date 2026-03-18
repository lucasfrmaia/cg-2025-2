package project_cg.transformations2d;

import project_cg.geometry.points.Point2D;
import project_cg.transformations.BaseTransformation2d;
import utils.Matrix;

public class Scale implements BaseTransformation2d {

    private final double sx;
    private final double sy;

    public Scale(double sx, double sy) {
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    public double[][] getTransformation() {
        return getMatrixScala(sx, sy);
    }

    public static double[][] getMatrixScala(double sx, double sy) {
        return new double[][]{
             { sx, 0, 0 },
             { 0, sy, 0 },
             { 0, 0,  1 }
        };
    }

    @Override
    public String toString() {
        return "S(" + sx + ", " + sy + ")";
    }

}
