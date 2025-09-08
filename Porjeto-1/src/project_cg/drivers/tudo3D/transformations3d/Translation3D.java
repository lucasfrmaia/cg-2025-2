package project_cg.drivers.tudo3D.transformations3d;

import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import view.utils.Matrix;

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

    // Exemplo de translação
    public static void main(String[] args) {
        // Cria um ponto 3D inicial
        Point3D originalPoint = new Point3D(2.0, 3.0, 4.0);
        System.out.println("Ponto original: " + originalPoint);

        // Define os valores de translação
        double tx = 5.0;
        double ty = -2.0;
        double tz = 3.0;

        // Aplica a translação ao ponto
        Point3D translatedPoint = Translation3D.translatePoint(originalPoint, tx, ty, tz);

        // Exibe o ponto após a translação
        System.out.println("Ponto após translação: " + translatedPoint);
    }
}
