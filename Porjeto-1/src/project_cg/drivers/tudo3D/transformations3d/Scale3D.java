package project_cg.drivers.tudo3D.transformations3d;

import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import view.utils.Matrix;

public class Scale3D {

    // Método para escalonar um ponto 3D usando uma matriz de escala
    public static Point3D scalePoint(Point3D point, double sx, double sy, double sz) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getMatrixScale(sx, sy, sz);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Retorna a matriz de escala 4x4 para 3D
    public static double[][] getMatrixScale(double sx, double sy, double sz) {
        return new double[][]{
            { sx, 0, 0, 0 },
            { 0, sy, 0, 0 },
            { 0, 0, sz, 0 },
            { 0, 0,  0, 1 }
        };
    }
    
    public static void main(String[] args) {
        // Cria um ponto 3D inicial
        Point3D originalPoint = new Point3D(2.0, 3.0, 4.0);
        System.out.println("Ponto original: " + originalPoint);

        // Define os valores de escala
        double sx = 2.0;
        double sy = 3.0;
        double sz = 0.5;

        // Aplica a escala ao ponto
        Point3D scaledPoint = Scale3D.scalePoint(originalPoint, sx, sy, sz);

        // Exibe o ponto após a escala
        System.out.println("Ponto após escala: " + scaledPoint);
    }

}
