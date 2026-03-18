package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;

import project_cg.geometry.planeCartesians.cartesiansPlane.CartesianPlane2D;
import project_cg.geometry.points.Point2D;
import project_cg.primitives.MidpointLine;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CartesianPlane2DWithViewport extends CartesianPlane2D {

    protected static final int WORLD_X_MIN = -200;
    protected static final int WORLD_Y_MIN = -200;
    protected static final int WORLD_X_MAX = 200;
    protected static final int WORLD_Y_MAX = 200;
    public final ViewportWindow viewportWindow;

    public CartesianPlane2DWithViewport() {
        int viewportWidth = 400;  // Largura da viewport
        int viewportHeight = 400; // Altura da viewport

        this.viewportWindow = new ViewportWindow(viewportWidth, viewportHeight);
        this.drawCartesianPlane();
    }

    public void updateViewport() {
        viewportWindow.updateViewport(this, WORLD_X_MIN, WORLD_Y_MIN, WORLD_X_MAX, WORLD_Y_MAX);
    }


    @Override
    public void clear() {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.drawCartesianPlane();
        this.updateViewport();
    }

    @Override
    public CartesianPlane2DWithViewport reset() {
        this.clear();
        return this;
    }

}
