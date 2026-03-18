package project_cg.transformations3d;

import project_cg.geometry.points.Point3D;
import utils.Matrix;

public class Reflection3D {

    public enum Type {
        IN_XY,
        IN_XZ,
        IN_YZ,
        IN_ORIGIN
    }

    private final Type type;

    public Reflection3D(Type type) {
        this.type = type;
    }

    public double[][] getTransformation() {
        switch (type) {
            case IN_XY:
                return getReflectionMatrixInXY();
            case IN_XZ:
                return getReflectionMatrixInXZ();
            case IN_YZ:
                return getReflectionMatrixInYZ();
            case IN_ORIGIN:
                return getReflectionMatrixInOrigin();
            default:
                throw new IllegalArgumentException("Tipo de reflexao 3D desconhecido: " + type);
        }
    }

    // Reflete o ponto em relação ao plano XY
    public static Point3D reflectInXY(Point3D point) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getReflectionMatrixInXY();
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Reflete o ponto em relação ao plano XZ
    public static Point3D reflectInXZ(Point3D point) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getReflectionMatrixInXZ();
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Reflete o ponto em relação ao plano YZ
    public static Point3D reflectInYZ(Point3D point) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getReflectionMatrixInYZ();
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Reflete o ponto em relação à origem
    public static Point3D reflectInOrigin(Point3D point) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getReflectionMatrixInOrigin();
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Matriz de reflexão no plano XY (inverte Z)
    public static double[][] getReflectionMatrixInXY() {
        return new double[][] {
            { 1, 0, 0, 0 },
            { 0, 1, 0, 0 },
            { 0, 0, -1, 0 },
            { 0, 0, 0, 1 }
        };
    }

    // Matriz de reflexão no plano XZ (inverte Y)
    public static double[][] getReflectionMatrixInXZ() {
        return new double[][] {
            { 1, 0, 0, 0 },
            { 0, -1, 0, 0 },
            { 0, 0, 1, 0 },
            { 0, 0, 0, 1 }
        };
    }

    // Matriz de reflexão no plano YZ (inverte X)
    public static double[][] getReflectionMatrixInYZ() {
        return new double[][] {
            { -1, 0, 0, 0 },
            { 0, 1, 0, 0 },
            { 0, 0, 1, 0 },
            { 0, 0, 0, 1 }
        };
    }

    // Matriz de reflexão na origem (inverte X, Y e Z)
    public static double[][] getReflectionMatrixInOrigin() {
        return new double[][] {
            { -1, 0, 0, 0 },
            { 0, -1, 0, 0 },
            { 0, 0, -1, 0 },
            { 0, 0, 0, 1 }
        };
    }

    @Override
    public String toString() {
        switch (type) {
            case IN_XY:
                return "Ref(XY)";
            case IN_XZ:
                return "Ref(XZ)";
            case IN_YZ:
                return "Ref(YZ)";
            case IN_ORIGIN:
                return "Ref(O)";
            default:
                return "Ref(?)";
        }
    }

}
