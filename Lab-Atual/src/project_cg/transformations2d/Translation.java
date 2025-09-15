package project_cg.transformations2d;

import project_cg.geometry.points.Point2D;
import view.utils.Matrix;

public class Translation {

    public static Point2D translatePoint(Point2D point, double tx, double ty) {
        double[][] pointHomogeneous = new double[][] {
                { point.x, point.y, 1 },
        };

        double[][] matrix = getMatrixTranslation(tx, ty);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point2D(
                result[0][0],
                result[0][1]
        );
    }

    public static double[][] getMatrixTranslation(double tx, double ty) {
        return new double[][] {
                { 1, 0, 0 },
                { 0, 1, 0 },
                { tx, ty, 1 }
        };
    }



}
