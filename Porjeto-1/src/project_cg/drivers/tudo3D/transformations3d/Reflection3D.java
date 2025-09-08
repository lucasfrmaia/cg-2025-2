package project_cg.drivers.tudo3D.transformations3d;

import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import view.utils.Matrix;

public class Reflection3D {

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
    
    public static void main(String[] args) {
        Point3D originalPoint = new Point3D(1.0, 2.0, 3.0);
        System.out.println("Ponto original: " + originalPoint);

        // Reflexão no plano XY
        Point3D reflectedXY = Reflection3D.reflectInXY(originalPoint);
        System.out.println("Reflexão no plano XY: " + reflectedXY);

        // Reflexão no plano XZ
        Point3D reflectedXZ = Reflection3D.reflectInXZ(originalPoint);
        System.out.println("Reflexão no plano XZ: " + reflectedXZ);

        // Reflexão no plano YZ
        Point3D reflectedYZ = Reflection3D.reflectInYZ(originalPoint);
        System.out.println("Reflexão no plano YZ: " + reflectedYZ);

        // Reflexão na origem
        Point3D reflectedOrigin = Reflection3D.reflectInOrigin(originalPoint);
        System.out.println("Reflexão na origem: " + reflectedOrigin);
    }
}
