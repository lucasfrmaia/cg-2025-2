package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.points.Point2D;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QueuedTransformationsPlane extends CartesianPlane2DWithViewport {

    private final Map<String, List<TransformationOperation>> pendingTransformations;

    public QueuedTransformationsPlane() {
        this.pendingTransformations = new LinkedHashMap<>();
    }

    public void queueTransformation(String figureId, TransformationOperation operation) {
        if (figureId == null || figureId.isBlank()) {
            throw new IllegalArgumentException("Selecione uma figura valida para enfileirar a transformacao.");
        }

        Objects.requireNonNull(operation, "A transformacao nao pode ser nula.");

        pendingTransformations
                .computeIfAbsent(figureId, ignored -> new ArrayList<>())
                .add(operation);
    }

    public int getPendingCount(String figureId) {
        if (figureId == null || figureId.isBlank()) {
            return 0;
        }

        return pendingTransformations.getOrDefault(figureId, List.of()).size();
    }

    public void applyQueuedTransformations(BaseFigure figure) {
        if (figure == null) {
            throw new IllegalArgumentException("A figura selecionada nao foi encontrada.");
        }

        String figureId = figure.getID();
        List<TransformationOperation> operations = pendingTransformations.get(figureId);

        if (operations == null || operations.isEmpty()) {
            throw new IllegalStateException("Nao ha transformacoes pendentes para a figura selecionada.");
        }

        List<TransformationOperation> operationsSnapshot = new ArrayList<>(operations);

        Point2D focalPoint = getFirstPointAsFocalPoint(figure);

        figure.getVertex(point -> {
            Point2D transformedPoint = new Point2D(
                    point.getX() - focalPoint.getX(),
                    point.getY() - focalPoint.getY()
            );

            for (TransformationOperation operation : operationsSnapshot) {
                transformedPoint = operation.apply(transformedPoint);
            }

            transformedPoint = new Point2D(
                    transformedPoint.getX() + focalPoint.getX(),
                    transformedPoint.getY() + focalPoint.getY()
            );

            point.updatePoint(transformedPoint);
        });

        pendingTransformations.remove(figureId);
    }

    private Point2D getFirstPointAsFocalPoint(BaseFigure figure) {
        final Point2D[] firstPoint = {null};

        figure.getVertex(point -> {
            if (firstPoint[0] == null) {
                firstPoint[0] = new Point2D(point.getX(), point.getY());
            }
        });

        if (firstPoint[0] == null) {
            throw new IllegalStateException("Nao foi possivel obter o ponto focal da figura.");
        }

        return firstPoint[0];
    }

    public void clearQueuedTransformations(String figureId) {
        if (figureId == null || figureId.isBlank()) {
            return;
        }

        pendingTransformations.remove(figureId);
    }

    public void clearAllQueuedTransformations() {
        pendingTransformations.clear();
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

    @FunctionalInterface
    public interface TransformationOperation {
        Point2D apply(Point2D point);
    }
}
