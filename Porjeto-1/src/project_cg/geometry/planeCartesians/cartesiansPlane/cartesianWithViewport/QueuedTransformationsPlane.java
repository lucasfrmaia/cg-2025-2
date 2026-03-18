package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;

import project_cg.geometry.clipping.SutherlandHodgmanLineClipper;
import project_cg.geometry.figures.Square;
import project_cg.geometry.points.Point2D;
import project_cg.primitives.MidpointLine;
import project_cg.transformations2d.Translation;
import utils.Matrix;

import java.util.ArrayList;
import java.util.List;

public class QueuedTransformationsPlane extends CartesianPlane2DWithViewport {

    private final List<double[][]> pendingTransformationMatrices;
    private final List<LineSegment> clippedSquareSegments;

    public QueuedTransformationsPlane() {
        this.pendingTransformationMatrices = new ArrayList<>();
        this.clippedSquareSegments = new ArrayList<>();
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

        updateClippedSegments(square);

        clearAllQueuedTransformations();
    }

    public boolean hasClippedSegments() {
        return !clippedSquareSegments.isEmpty();
    }

    public void drawClippedSquare(int rgb) {
        MidpointLine line = new MidpointLine(point -> setPixel(point, rgb));

        for (LineSegment segment : clippedSquareSegments) {
            line.desenhaLinha(segment.start, segment.end);
        }
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

    private void updateClippedSegments(Square square) {
        clippedSquareSegments.clear();

        List<Point2D> vertices = extractSquareVertices(square);
        if (vertices.size() < 2) {
            return;
        }

        SutherlandHodgmanLineClipper clipper = new SutherlandHodgmanLineClipper(
                getViewportWorldXMin(),
                getViewportWorldYMin(),
                getViewportWorldXMax(),
                getViewportWorldYMax()
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
                clippedSquareSegments.add(new LineSegment(clipped[0], clipped[1]));
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
        super.clear();
        clippedSquareSegments.clear();
        clearAllQueuedTransformations();
    }

    @Override
    public QueuedTransformationsPlane reset() {
        super.reset();
        clippedSquareSegments.clear();
        clearAllQueuedTransformations();
        return this;
    }

    private static class LineSegment {
        private final Point2D start;
        private final Point2D end;

        private LineSegment(Point2D start, Point2D end) {
            this.start = start;
            this.end = end;
        }
    }

}
