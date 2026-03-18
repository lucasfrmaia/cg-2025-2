package project_cg.transformations3d;

import project_cg.geometry.points.Point3D;
import utils.Matrix;

public class Shear3D {

    public enum Axis {
        X,
        Y,
        Z
    }

    private final Axis axis;
    private final double factor1;
    private final double factor2;

    public Shear3D(Axis axis, double factor1, double factor2) {
        this.axis = axis;
        this.factor1 = factor1;
        this.factor2 = factor2;
    }

    public double[][] getTransformation() {
        switch (axis) {
            case X:
                return getMatrixShearX(factor1, factor2);
            case Y:
                return getMatrixShearY(factor1, factor2);
            case Z:
                return getMatrixShearZ(factor1, factor2);
            default:
                throw new IllegalArgumentException("Eixo de cisalhamento desconhecido: " + axis);
        }
    }

    // Método para aplicar cisalhamento no eixo X em relação a Y e Z
    public static Point3D shearX(Point3D point, double shy, double shz) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getMatrixShearX(shy, shz);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Método para aplicar cisalhamento no eixo Y em relação a X e Z
    public static Point3D shearY(Point3D point, double shx, double shz) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getMatrixShearY(shx, shz);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Método para aplicar cisalhamento no eixo Z em relação a X e Y
    public static Point3D shearZ(Point3D point, double shx, double shy) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getMatrixShearZ(shx, shy);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Retorna a matriz de cisalhamento para o eixo X em relação a Y e Z
    public static double[][] getMatrixShearX(double shy, double shz) {
        return new double[][] {
            { 1, shy, shz, 0 },
            { 0, 1, 0, 0 },
            { 0, 0, 1, 0 },
            { 0, 0, 0, 1 }
        };
    }

    // Retorna a matriz de cisalhamento para o eixo Y em relação a X e Z
    public static double[][] getMatrixShearY(double shx, double shz) {
        return new double[][] {
            { 1, 0, 0, 0 },
            { shx, 1, shz, 0 },
            { 0, 0, 1, 0 },
            { 0, 0, 0, 1 }
        };
    }

    // Retorna a matriz de cisalhamento para o eixo Z em relação a X e Y
    public static double[][] getMatrixShearZ(double shx, double shy) {
        return new double[][] {
            { 1, 0, 0, 0 },
            { 0, 1, 0, 0 },
            { shx, shy, 1, 0 },
            { 0, 0, 0, 1 }
        };
    }

    @Override
    public String toString() {
        return "Sh" + axis + "(" + factor1 + ", " + factor2 + ")";
    }
    

}

