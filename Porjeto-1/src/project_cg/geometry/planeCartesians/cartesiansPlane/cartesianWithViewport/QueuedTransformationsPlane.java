package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;

import project_cg.geometry.clipping.SutherlandHodgmanLineClipper;
import project_cg.geometry.figures.Square;
import project_cg.geometry.points.Point2D;
import project_cg.transformations.BaseTransformation2d;
import project_cg.transformations2d.Reflection;
import project_cg.transformations2d.Translation;
import utils.Matrix;

import java.util.ArrayList;
import java.util.List;

public class QueuedTransformationsPlane extends CartesianPlane2DWithViewport {

    private final List<BaseTransformation2d> pendingTransformations;
    private final List<Point2D[]> clippedSquareSegments;

    public QueuedTransformationsPlane() {
        this.pendingTransformations = new ArrayList<>();
        this.clippedSquareSegments = new ArrayList<>();
    }

    public void queueTransformation(BaseTransformation2d transformation) {
        if (transformation == null) {
            throw new IllegalArgumentException("A transformacao nao pode ser nula.");
        }

        pendingTransformations.add(transformation);
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

        for (BaseTransformation2d transformation : pendingTransformations) {
            applyTransformationInQueueOrder(square, transformation);
        }

        updateClippedSegmentsWithSutherlandHodgman(square);

        clearAllQueuedTransformations();
    }

    private void applyTransformationInQueueOrder(Square square, BaseTransformation2d transformation) {
        double[][] transformationMatrix = transformation.getTransformation();

        if (!(transformation instanceof Translation) && !(transformation instanceof Reflection)) {
            Point2D focalPoint = getFirstPointAsFocalPoint(square);
            transformationMatrix = composeMatrixAroundFocalPoint(transformationMatrix, focalPoint);
        }

        applyMatrixToSquare(square, transformationMatrix);
    }

    private double[][] composeMatrixAroundFocalPoint(double[][] transformationMatrix, Point2D focalPoint) {
        double[][] toOrigin = new Translation(-focalPoint.getX(), -focalPoint.getY()).getTransformation();
        double[][] backToFocal = new Translation(focalPoint.getX(), focalPoint.getY()).getTransformation();
        return Matrix.multiply(Matrix.multiply(toOrigin, transformationMatrix), backToFocal);
    }

    private void applyMatrixToSquare(Square square, double[][] matrix) {
        square.getVertex(point -> {
            double[][] pointHomogeneous = new double[][] {
                    { point.getX(), point.getY(), 1 }
            };

            double[][] result = Matrix.multiply(pointHomogeneous, matrix);

            Point2D transformedPoint = new Point2D(result[0][0], result[0][1]);
            point.updatePoint(transformedPoint);
        });
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
    public void updateViewport() {
        if (clippedSquareSegments.isEmpty()) {
            super.updateViewport();
            return;
        }

        viewportWindow.updateViewportWithSegments(
                clippedSquareSegments,
                WORLD_X_MIN,
                WORLD_Y_MIN,
                WORLD_X_MAX,
                WORLD_Y_MAX,
                java.awt.Color.RED.getRGB()
        );
    }

    private void updateClippedSegmentsWithSutherlandHodgman(Square square) {
        clippedSquareSegments.clear();

        List<Point2D> vertices = extractSquareVertices(square);
        if (vertices.size() < 2) {
            return;
        }

        SutherlandHodgmanLineClipper clipper = new SutherlandHodgmanLineClipper(
                WORLD_X_MIN,
                WORLD_Y_MIN,
                WORLD_X_MAX,
                WORLD_Y_MAX
        );

        for (int i = 0; i < vertices.size(); i++) {
            Point2D start = vertices.get(i);
            Point2D end = vertices.get((i + 1) % vertices.size());

            Point2D[] clipped = clipper.clipLine(
                    (int) Math.round(start.x),
                    (int) Math.round(start.y),
                    (int) Math.round(end.x),
                    (int) Math.round(end.y)
            );

            if (clipped != null) {
                clippedSquareSegments.add(new Point2D[] {
                        new Point2D(clipped[0].getX(), clipped[0].getY()),
                        new Point2D(clipped[1].getX(), clipped[1].getY())
                });
            }
        }
    }

    private List<Point2D> extractSquareVertices(Square square) {
        List<Point2D> vertices = new ArrayList<>();

        square.getVertex(point -> vertices.add(new Point2D(point.getX(), point.getY())));

        return vertices;
    }

    @Override
    public void clear() {
        clippedSquareSegments.clear();
        super.clear();
    }

    @Override
    public QueuedTransformationsPlane reset() {
        super.reset();
        clippedSquareSegments.clear();
        clearAllQueuedTransformations();
        return this;
    }

}
