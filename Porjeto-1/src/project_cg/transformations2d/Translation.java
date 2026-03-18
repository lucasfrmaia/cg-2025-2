package project_cg.transformations2d;

import project_cg.geometry.points.Point2D;
import project_cg.transformations.BaseTransformation2d;
import utils.Matrix;

public class Translation implements BaseTransformation2d {

    private double tx;
    private double ty;

    public Translation(double tx, double ty) {
        this.tx = tx;
        this.ty = ty;
    }

    @Override
    public double[][] getTransformation() {
        return getMatrixTranslation(tx, ty);
    }

    public static double[][] getMatrixTranslation(double tx, double ty) {
        return new double[][] {
                { 1, 0, 0 },
                { 0, 1, 0 },
                { tx, ty, 1 }
        };
    }

    @Override
    public String toString() {
        return "T(" + tx + ", " + ty + ")";
    }

}
