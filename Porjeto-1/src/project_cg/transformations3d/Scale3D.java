package project_cg.transformations3d;

import project_cg.geometry.points.Point3D;
import utils.Matrix;

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
    


}
