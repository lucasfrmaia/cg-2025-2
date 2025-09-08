package project_cg.transformations2d;

import project_cg.geometry.points.Point2D;
import view.utils.Matrix;

public class Reflection  {

    public static Point2D reflectPpintInX(Point2D point) {
        double[][] pointHomogeneous = new double[][] {
                { point.x, point.y, 1 },
        };

        double[][] matrix = getReflectionMatrixInX();
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point2D(
                result[0][0],
                result[0][1]
        );
    }

    public static Point2D reflectPpintInY(Point2D point) {
        double[][] pointHomogeneous = new double[][] {
                { point.x, point.y, 1 },
        };

        double[][] matrix = getReflectionMatrixInY();
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point2D(
                result[0][0],
                result[0][1]
        );
    }

    public static Point2D reflectPpintInOrigin(Point2D point) {
        double[][] pointHomogeneous = new double[][] {
                { point.x, point.y, 1 },
        };

        double[][] matrix = getReflectionMatrixInOrigin();
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point2D(
                result[0][0],
                result[0][1]
        );
    }

    public static double[][] getReflectionMatrixInOrigin() {
        return new double[][]{
                { -1, 0, 0 },
                { 0, -1, 0 },
                { 0, 0, 1 }
        };
    }

    public static double[][] getReflectionMatrixInX() {
        return new double[][]{
                { 1, 0, 0 },
                { 0, -1, 0 },
                { 0, 0, 1 }
        };
    }

    public static double[][] getReflectionMatrixInY() {
        return new double[][]{
                {-1, 0, 0 },
                { 0, 1, 0 },
                { 0, 0, 1 }
        };
    }


}
