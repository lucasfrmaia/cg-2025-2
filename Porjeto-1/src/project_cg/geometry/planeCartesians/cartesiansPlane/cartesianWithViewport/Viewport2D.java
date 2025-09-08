package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;


import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane;

import project_cg.geometry.points.Point2D;
import utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Viewport2D {

    private final int viewportWidth;
    private final int viewportHeight;
    private final int viewportX;
    private final int viewportY;
    private BufferedImage image;

    public Viewport2D(int x, int y, int width, int height) {
        this.viewportX = x;
        this.viewportY = y;
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void clearViewport() {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK); // Define a cor de fundo
        g.fillRect(0, 0, viewportWidth, viewportHeight);
        g.dispose();
    }

    public void renderFromCartesian(BaseCartesianPlane plane, int worldXMin, int worldYMin, int worldXMax, int worldYMax) {
        clearViewport();

        for (int x = worldXMin; x <= worldXMax; x++) {
            for (int y = worldYMin; y <= worldYMax; y++) {

                int cartesianRGB = plane.getPixel(x, y);

                // Ignora apenas os pixels que pertencem aos eixos
                if (cartesianRGB == Constants.COLOR_LINES_CARTESIAN_PLANE) {
                    continue;
                }

                if (cartesianRGB != Color.BLACK.getRGB()) { // Apenas pixels não vazios
                    Point2D viewportPoint = mapToViewport(x, y, worldXMin, worldYMin, worldXMax, worldYMax);
                    if (viewportPoint != null) {
                        setPixel(viewportPoint, cartesianRGB);
                    }
                }
            }
        }
    }

    public Point2D mapToViewport(int x, int y, int worldXMin, int worldYMin, int worldXMax, int worldYMax) {
        double normalizedX = (double) (x - worldXMin) / (worldXMax - worldXMin);
        double normalizedY = (double) (y - worldYMin) / (worldYMax - worldYMin);

        int viewportX = (int) (normalizedX * viewportWidth);
        int viewportY = (int) ((1 - normalizedY) * viewportHeight); // Inverte o eixo Y para a tela

        if (viewportX >= 0 && viewportX < viewportWidth && viewportY >= 0 && viewportY < viewportHeight) {
            return new Point2D(viewportX, viewportY);
        }

        return null;
    }

    public void setPixel(Point2D point, int rgb) {
        if (point.x >= 0 && point.x < viewportWidth && point.y >= 0 && point.y < viewportHeight) {
            image.setRGB((int) point.x, (int) point.y, rgb);
        }
    }

    public void draw(Graphics g) {
        g.drawImage(image, viewportX, viewportY, null);
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public int getWidth() {
        return viewportWidth;
    }

    public int getHeight() {
        return viewportHeight;
    }
}
