package project_cg.drivers.tudo3D.transformations3d;

import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import view.utils.Matrix;

public class Shear3D {

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
    
    public static void main(String[] args) {
        Point3D originalPoint = new Point3D(2.0, 3.0, 4.0);
        System.out.println("Ponto original: " + originalPoint);

        // Cisalhamento no eixo X com fatores de cisalhamento para Y e Z
        double shy = 1.5;
        double shz = 0.5;
        Point3D shearedPointX = Shear3D.shearX(originalPoint, shy, shz);
        System.out.println("Ponto após cisalhamento no eixo X: " + shearedPointX);

        // Cisalhamento no eixo Y com fatores de cisalhamento para X e Z
        double shx = 1.0;
        Point3D shearedPointY = Shear3D.shearY(originalPoint, shx, shz);
        System.out.println("Ponto após cisalhamento no eixo Y: " + shearedPointY);

        // Cisalhamento no eixo Z com fatores de cisalhamento para X e Y
        Point3D shearedPointZ = Shear3D.shearZ(originalPoint, shx, shy);
        System.out.println("Ponto após cisalhamento no eixo Z: " + shearedPointZ);
    }
}

