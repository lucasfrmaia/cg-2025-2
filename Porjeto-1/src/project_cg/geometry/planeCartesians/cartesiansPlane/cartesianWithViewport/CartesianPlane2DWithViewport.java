package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;

import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane2D;
import project_cg.geometry.planeCartesians.cartesiansPlane.CartesianPlane2D;
import project_cg.geometry.points.Point2D;
import project_cg.primitives.MidpointLine;
import utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CartesianPlane2DWithViewport extends CartesianPlane2D {

    protected static final int WORLD_X_MIN = -200;
    protected static final int WORLD_Y_MIN = -200;
    protected static final int WORLD_X_MAX = 200;
    protected static final int WORLD_Y_MAX = 200;
    private static final int VIEWPORT_BORDER_COLOR = Color.CYAN.getRGB();

    public final ViewportWindow viewportWindow;

    public CartesianPlane2DWithViewport() {
        int viewportWidth = 400;  // Largura da viewport
        int viewportHeight = 400; // Altura da viewport

        this.viewportWindow = new ViewportWindow(viewportWidth, viewportHeight);
        this.drawCartesianPlane();
        drawViewportBounds();
    }

    public void updateViewport() {
        viewportWindow.updateViewport(this, WORLD_X_MIN, WORLD_Y_MIN, WORLD_X_MAX, WORLD_Y_MAX);
    }

    public void drawViewportBounds() {
        Point2D topLeft = new Point2D(WORLD_X_MIN, WORLD_Y_MAX);
        Point2D topRight = new Point2D(WORLD_X_MAX, WORLD_Y_MAX);
        Point2D bottomRight = new Point2D(WORLD_X_MAX, WORLD_Y_MIN);
        Point2D bottomLeft = new Point2D(WORLD_X_MIN, WORLD_Y_MIN);

        MidpointLine line = new MidpointLine(point -> setPixel(point, VIEWPORT_BORDER_COLOR));
        line.desenhaLinha(topLeft, topRight);
        line.desenhaLinha(topRight, bottomRight);
        line.desenhaLinha(bottomRight, bottomLeft);
        line.desenhaLinha(bottomLeft, topLeft);
    }

    public int getViewportWorldXMin() {
        return WORLD_X_MIN;
    }

    public int getViewportWorldYMin() {
        return WORLD_Y_MIN;
    }

    public int getViewportWorldXMax() {
        return WORLD_X_MAX;
    }

    public int getViewportWorldYMax() {
        return WORLD_Y_MAX;
    }

    @Override
    public void clear() {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.drawCartesianPlane();
        this.drawViewportBounds();
        this.updateViewport();
    }

    @Override
    public CartesianPlane2DWithViewport reset() {
        this.clear();
        return this;
    }

}
