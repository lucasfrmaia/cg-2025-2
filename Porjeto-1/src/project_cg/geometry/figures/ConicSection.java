package project_cg.geometry.figures;

import project_cg.geometry.points.Point2D;

import java.awt.*;

public class ConicSection extends BaseFigure {

    private final ConicType type;
    private final int centerX;
    private final int centerY;
    private final double parameterA;
    private final double parameterB;
    private final int range;
    private final int samples;

    public ConicSection(ConicType type, int centerX, int centerY, double parameterA, double parameterB, int range, int samples) {
        this.type = type;
        this.centerX = centerX;
        this.centerY = centerY;
        this.parameterA = parameterA;
        this.parameterB = parameterB;
        this.range = range;
        this.samples = samples;

        generatePoints();
    }

    @Override
    public String getID() {
        return String.format("Secao Conica [%s]", type.getLabel());
    }

    @Override
    public int getColor() {
        return Color.ORANGE.getRGB();
    }

    @Override
    public void generatePoints() {
        points.clear();

        switch (type) {
            case ELLIPSE -> generateEllipsePoints();
            case PARABOLA -> generateParabolaPoints();
            case HYPERBOLA -> generateHyperbolaPoints();
        }
    }

    private void generateEllipsePoints() {
        if (parameterA <= 0 || parameterB <= 0) {
            throw new IllegalArgumentException("Na elipse, os parametros a e b devem ser maiores que zero.");
        }

        int localSamples = Math.max(40, samples);

        for (int i = 0; i < localSamples; i++) {
            double theta = (2.0 * Math.PI * i) / localSamples;
            double x = centerX + (parameterA * Math.cos(theta));
            double y = centerY + (parameterB * Math.sin(theta));
            points.add(new Point2D(x, y));
        }
    }

    private void generateParabolaPoints() {
        if (Math.abs(parameterA) < 1e-9) {
            throw new IllegalArgumentException("Na parabola, o parametro focal p nao pode ser zero.");
        }

        int localSamples = Math.max(30, samples);
        double yMax = Math.max(1, range);

        for (int i = -localSamples; i <= localSamples; i++) {
            double y = (yMax * i) / localSamples;
            double x = (y * y) / (2.0 * parameterA);
            points.add(new Point2D(centerX + x, centerY + y));
        }
    }

    private void generateHyperbolaPoints() {
        if (parameterA <= 0 || parameterB <= 0) {
            throw new IllegalArgumentException("Na hiperbole, os parametros a e b devem ser maiores que zero.");
        }

        double xLimit = Math.max(parameterA + 1.0, range);
        int localSamples = Math.max(30, samples);

        for (int i = 0; i <= localSamples; i++) {
            double x = parameterA + ((xLimit - parameterA) * i / localSamples);
            double inside = ((x * x) / (parameterA * parameterA)) - 1.0;

            if (inside < 0) {
                continue;
            }

            double y = parameterB * Math.sqrt(inside);

            points.add(new Point2D(centerX + x, centerY + y));
            points.add(new Point2D(centerX + x, centerY - y));
            points.add(new Point2D(centerX - x, centerY + y));
            points.add(new Point2D(centerX - x, centerY - y));
        }
    }

    public enum ConicType {
        ELLIPSE("Elipse"),
        PARABOLA("Parabola"),
        HYPERBOLA("Hiperbole");

        private final String label;

        ConicType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
