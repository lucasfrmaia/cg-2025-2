package project_cg.transformations2d;

import project_cg.geometry.points.Point2D;
import view.utils.Matrix;

public class Scale {

    public static Point2D scalePoint(Point2D point, double sx, double sy) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, 1 },
        };

        double[][] matrix = getMatrixScala(sx, sy);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point2D(
           result[0][0],
           result[0][1]
        );
    }

    public static double[][] getMatrixScala(double sx, double sy) {
        return new double[][]{
             { sx, 0, 0 },
             { 0, sy, 0 },
             { 0, 0,  1 }
        };
    }

}
