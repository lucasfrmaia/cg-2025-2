package project_cg.transformations3d;

import project_cg.geometry.points.Point3D;
import utils.Matrix;

public class Rotation3D {

    public enum Axis {
        X,
        Y,
        Z
    }

    private final Axis axis;
    private final double angle;

    public Rotation3D(Axis axis, double angle) {
        this.axis = axis;
        this.angle = angle;
    }

    public double[][] getTransformation() {
        switch (axis) {
            case X:
                return getMatrixRotationX(angle);
            case Y:
                return getMatrixRotationY(angle);
            case Z:
                return getMatrixRotationZ(angle);
            default:
                throw new IllegalArgumentException("Eixo de rotacao desconhecido: " + axis);
        }
    }

    // Rotaciona o ponto em torno do eixo X
    public static Point3D rotateX(Point3D point, double angle) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getMatrixRotationX(angle);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Rotaciona o ponto em torno do eixo Y
    public static Point3D rotateY(Point3D point, double angle) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getMatrixRotationY(angle);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Rotaciona o ponto em torno do eixo Z
    public static Point3D rotateZ(Point3D point, double angle) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getMatrixRotationZ(angle);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Retorna a matriz de rotação em torno do eixo X
    public static double[][] getMatrixRotationX(double angle) {
        double radians = Math.toRadians(angle);
        return new double[][] {
            { 1, 0, 0, 0 },
            { 0, Math.cos(radians), -Math.sin(radians), 0 },
            { 0, Math.sin(radians), Math.cos(radians), 0 },
            { 0, 0, 0, 1 }
        };
    }

    // Retorna a matriz de rotação em torno do eixo Y
    public static double[][] getMatrixRotationY(double angle) {
        double radians = Math.toRadians(-angle);
        return new double[][] {
            { Math.cos(radians), 0, Math.sin(radians), 0 },
            { 0, 1, 0, 0 },
            { -Math.sin(radians), 0, Math.cos(radians), 0 },
            { 0, 0, 0, 1 }
        };
    }

    // Retorna a matriz de rotação em torno do eixo Z
    public static double[][] getMatrixRotationZ(double angle) {
        double radians = Math.toRadians(angle);
        return new double[][] {
            { Math.cos(radians), -Math.sin(radians), 0, 0 },
            { Math.sin(radians), Math.cos(radians), 0, 0 },
            { 0, 0, 1, 0 },
            { 0, 0, 0, 1 }
        };
    }

    @Override
    public String toString() {
        return "R" + axis + "(" + angle + ")";
    }

}
