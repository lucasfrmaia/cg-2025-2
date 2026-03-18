package project_cg.transformations3d;

import project_cg.geometry.points.Point3D;
import utils.Matrix;

public class Translation3D {

	// Método para transladar um ponto 3D usando uma matriz de translação
    public static Point3D translatePoint(Point3D point, double tx, double ty, double tz) {
        double[][] pointHomogeneous = new double[][] {
            { point.x, point.y, point.z, 1 }
        };

        double[][] matrix = getMatrixTranslation(tx, ty, tz);
        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(
            result[0][0],
            result[0][1],
            result[0][2]
        );
    }

    // Retorna a matriz de translação 4x4 para 3D
    public static double[][] getMatrixTranslation(double tx, double ty, double tz) {
        return new double[][] {
        	{ 1, 0, 0, 0 },
            { 0, 1, 0, 0 },
            { 0, 0, 1, 0 },
            { tx, ty, tz, 1 }
        };
    }


}
