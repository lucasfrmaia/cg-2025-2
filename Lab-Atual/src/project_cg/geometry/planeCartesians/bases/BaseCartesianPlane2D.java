package project_cg.geometry.planeCartesians.bases;

import project_cg.geometry.planeCartesians.cartesiansPlane.CartesianPlane2D;
import utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class BaseCartesianPlane2D extends BaseCartesianPlane {

    protected BufferedImage image;

    protected int height;

    protected int width;

    public BaseCartesianPlane2D(int width, int height) {
        this.height = height;
        this.width = width;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public BaseCartesianPlane2D reset() {
        return new CartesianPlane2D();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void drawCartesianPlane() {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, Constants.BACKGROUND_CARTESIAN_PLANE);
            }
        }

        for (int x = 0; x < width; x++) {
            image.setRGB(x, height / 2, Constants.COLOR_LINES_CARTESIAN_PLANE);
        }

        for (int y = 0; y < height; y++) {
            image.setRGB(width / 2, y, Constants.COLOR_LINES_CARTESIAN_PLANE);
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

}
