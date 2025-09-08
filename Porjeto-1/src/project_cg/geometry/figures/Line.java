package project_cg.geometry.figures;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BaseLine;

public class Line extends BaseFigure {

    private Point2D start, end;

    private BaseLine lineAlgorithm;

    public Line(Point2D start, Point2D end, BaseLine lineAlgorithm) {
        this.start = start;
        this.end = end;
        this.lineAlgorithm = lineAlgorithm;

        this.generatePoints();
    }

    @Override
    public void generatePoints() {
        lineAlgorithm.setCallback((point2D -> this.points.add(point2D)));
        lineAlgorithm.desenhaLinha(start, end);
    }

    @Override
    public String getID() {
        return "Reta: [%s, %s]".formatted(start, end);
    }

}
