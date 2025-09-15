package project_cg.transformations2d;

import project_cg.geometry.points.Point2D;
import view.utils.Matrix;


public class Shear { //Cisalhamento

    public static Point2D shearX(Point2D point, double shx) {
        double[][] pointHomogeneous = new double[][] {
                { point.x, point.y, 1 },
        };

        double[][] matrix = getMatrixShearX(shx);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point2D(
                result[0][0],
                result[0][1]
        );
    }

    public static Point2D shearY(Point2D point, double shy) {
        double[][] pointHomogeneous = new double[][] {
                { point.x, point.y, 1 },
        };

        double[][] matrix = getMatrixShearY(shy);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point2D(
                result[0][0],
                result[0][1]
        );
    }

    public static Point2D shearXY(Point2D point, double shx, double shy) {
        double[][] pointHomogeneous = new double[][] {
                { point.x, point.y, 1 },
        };

        double[][] matrix = getMatrixShearXY(shx, shy);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point2D(
                result[0][0],
                result[0][1]
        );
    }


    public static double[][] getMatrixShearY(double shy) {
        return new double[][] {
                { 1, 0, 0 },
                { shy, 1, 0 },   // Shear along Y-axis
                { 0, 0, 1 }
        };
    }

    public static double[][] getMatrixShearX(double shx) {
        return new double[][] {
                { 1, shx, 0 },   // Shear along X-axis
                { 0, 1, 0 },
                { 0, 0, 1 }
        };
    }

    public static double[][] getMatrixShearXY(double shx, double shy) {
        return new double[][] {
                { 1, shx, 0 },   // Shear along X-axis
                { shy, 1, 0 },   // Shear along Y-axis
                { 0, 0, 1 }
        };
    }


}
