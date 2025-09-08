package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;

import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane2D;
import project_cg.geometry.planeCartesians.cartesiansPlane.CartesianPlane2D;
import project_cg.geometry.points.Point2D;
import utils.Constants;

import java.awt.image.BufferedImage;

public class CartesianPlane2DWithViewport extends CartesianPlane2D {

    public final ViewportWindow viewportWindow;

    public CartesianPlane2DWithViewport() {
        int viewportWidth = 400;  // Largura da viewport
        int viewportHeight = 400; // Altura da viewport

        this.viewportWindow = new ViewportWindow(viewportWidth, viewportHeight);
        this.drawCartesianPlane();
    }

    public void updateViewport() {
        int worldXMin = -500, worldYMin = -500, worldXMax = 500, worldYMax = 500; // Região da viewport
        viewportWindow.updateViewport(this, worldXMin, worldYMin, worldXMax, worldYMax);
    }

    @Override
    public void clear() {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.updateViewport();
        this.drawCartesianPlane();
    }

    @Override
    public CartesianPlane2DWithViewport reset() {
        this.clear();
        return this;
    }

}
