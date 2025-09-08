package project_cg.geometry.planeCartesians.cartesiansPlane;

import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane2D;
import project_cg.geometry.points.Point2D;
import utils.Constants;

import java.awt.image.BufferedImage;

public class CartesianPlane2D extends BaseCartesianPlane2D {

    public CartesianPlane2D() {
        super(Constants.WIDTH_CARTESIAN_PLANE, Constants.HEIGHT_CARTESIAN_PLANE);
        drawCartesianPlane();
    }

    @Override
    public int getPixel(int x, int y) {
        int screenX = x + image.getWidth() / 2;
        int screenY = image.getHeight() / 2 - y;

        if (screenX >= 0 && screenX < image.getWidth() && screenY >= 0 && screenY < image.getHeight()) {
            return image.getRGB(screenX, screenY);
        }

        return Constants.BACKGROUND_CARTESIAN_PLANE;
    }

    @Override
    public void setPixel(Point2D point, int rgb) {
        int screenX = (int) (point.x + image.getWidth() / 2);
        int screenY = (int) (image.getHeight() / 2 - point.y);

        if (screenX >= 0 && screenX < image.getWidth() && screenY >= 0 && screenY < image.getHeight()) {
            image.setRGB(screenX, screenY, rgb);
        }
    }

    @Override
    public void clear() {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.drawCartesianPlane();
    }

}
