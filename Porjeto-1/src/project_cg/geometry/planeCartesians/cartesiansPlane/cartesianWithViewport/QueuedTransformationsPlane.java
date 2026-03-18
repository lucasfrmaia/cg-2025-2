package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;

import project_cg.geometry.figures.Square;
import project_cg.geometry.points.Point2D;
import project_cg.transformations.BaseTransformation2d;
import project_cg.transformations2d.Translation;
import utils.Matrix;

import java.util.ArrayList;
import java.util.List;

public class QueuedTransformationsPlane extends CartesianPlane2DWithViewport {

    private final List<BaseTransformation2d> pendingTransformations;

    public QueuedTransformationsPlane() {
        this.pendingTransformations = new ArrayList<>();
    }

    public void queueTransformation(BaseTransformation2d transformation) {
        if (transformation == null) {
            throw new IllegalArgumentException("A transformacao nao pode ser nula.");
        }

        pendingTransformations.add(transformation);
    }

    public int getPendingTransformationsCount() {
        return pendingTransformations.size();
    }

    public String getPendingTransformationsSummary() {
        if (pendingTransformations.isEmpty()) {
            return "Nenhuma";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pendingTransformations.size(); i++) {
            if (i > 0) {
                builder.append(" -> ");
            }
            builder.append(pendingTransformations.get(i).toString());
        }

        return builder.toString();
    }
    
    public void applyQueuedTransformations(Square square) {
        if (square == null) {
            throw new IllegalArgumentException("O quadrado selecionado nao foi encontrado.");
        }

        if (pendingTransformations.isEmpty()) {
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

        multiplicationOrder.add(new Translation(-focalPoint.getX(), -focalPoint.getY()).getTransformation());
        for (BaseTransformation2d transformation : pendingTransformations) {
            multiplicationOrder.add(transformation.getTransformation());
        }
        multiplicationOrder.add(new Translation(focalPoint.getX(), focalPoint.getY()).getTransformation());

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
        pendingTransformations.clear();
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public QueuedTransformationsPlane reset() {
        super.reset();
        clearAllQueuedTransformations();
        return this;
    }

}
