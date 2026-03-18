package project_cg.geometry.planeCartesians.cartesiansPlane;

import project_cg.geometry.points.Point2D;

public interface LineClippingPlane {

    void setViewportSize(int width, int height);

    void addCustomLine(Point2D start, Point2D end);

    void generateRandomLines(int quantity);

    void applyClipping();

    int getOriginalLineCount();

    int getClippedLineCount();

    int getViewportWidthValue();

    int getViewportHeightValue();

    void setHideClippedSegments(boolean hideClippedSegments);

    boolean isHideClippedSegments();
}
