package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;

import project_cg.geometry.figures.Square;
import project_cg.geometry.points.Point2D;
import project_cg.transformations2d.Translation;
import utils.Matrix;

import java.util.ArrayList;
import java.util.List;

public class QueuedTransformationsPlane extends CartesianPlane2DWithViewport {

    private final List<double[][]> pendingTransformationMatrices;

    public QueuedTransformationsPlane() {
        this.pendingTransformationMatrices = new ArrayList<>();
    }

    public void queueTransformation(double[][] transformationMatrix) {
        if (transformationMatrix == null || transformationMatrix.length != 3 || transformationMatrix[0].length != 3) {
            throw new IllegalArgumentException("A matriz de transformacao deve ser 3x3.");
        }

        pendingTransformationMatrices.add(transformationMatrix);
    }
    
    public void applyQueuedTransformations(Square square) {
        if (square == null) {
            throw new IllegalArgumentException("O quadrado selecionado nao foi encontrado.");
        }

        if (pendingTransformationMatrices.isEmpty()) {
            throw new IllegalStateException("Nao ha transformacoes pendentes para o quadrado selecionado.");
        }

        Point2D focalPoint = getFirstPointAsFocalPoint(square);
        double[][] composedMatrix = buildComposedMatrixFromRightToLeft(focalPoint);

        square.getVertex(point -> {
            double[][] pointHomogeneous = new double[][] {
                    { point.getX(), point.getY(), 1 }
            };

            double[][] result = Matrix.multiply(pointHomogeneous, composedMatrix);

            Point2D transformedPoint = new Point2D(result[0][0], result[0][1]);
            point.updatePoint(transformedPoint);
        });

        clearAllQueuedTransformations();
    }

    private double[][] buildComposedMatrixFromRightToLeft(Point2D focalPoint) {
        List<double[][]> multiplicationOrder = new ArrayList<>();

        multiplicationOrder.add(Translation.getMatrixTranslation(-focalPoint.getX(), -focalPoint.getY()));
        multiplicationOrder.addAll(pendingTransformationMatrices);
        multiplicationOrder.add(Translation.getMatrixTranslation(focalPoint.getX(), focalPoint.getY()));

        double[][] composedMatrix = getIdentityMatrix3x3();

        for (double[][] matrix : multiplicationOrder) {
            composedMatrix = Matrix.multiply(composedMatrix, matrix);
        }

        return composedMatrix;
    }

    private double[][] getIdentityMatrix3x3() {
        return new double[][] {
                { 1, 0, 0 },
                { 0, 1, 0 },
                { 0, 0, 1 }
        };
    }

    private Point2D getFirstPointAsFocalPoint(Square square) {
        final Point2D[] firstPoint = {null};

        square.getVertex(point -> {
            if (firstPoint[0] == null) {
                firstPoint[0] = new Point2D(point.getX(), point.getY());
            }
        });

        if (firstPoint[0] == null) {
            throw new IllegalStateException("Nao foi possivel obter o ponto focal da figura.");
        }

        return firstPoint[0];
    }

    public void clearAllQueuedTransformations() {
        pendingTransformationMatrices.clear();
    }

    @Override
    public void clear() {
        super.clear();
        clearAllQueuedTransformations();
    }

    @Override
    public QueuedTransformationsPlane reset() {
        super.reset();
        clearAllQueuedTransformations();
        return this;
    }

}
