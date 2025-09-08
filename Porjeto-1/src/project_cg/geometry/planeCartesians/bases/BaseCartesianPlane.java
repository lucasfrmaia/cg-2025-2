package project_cg.geometry.planeCartesians.bases;

import project_cg.geometry.points.Point2D;
import view.utils.BaseJPanel;

import javax.swing.*;

public abstract class BaseCartesianPlane extends BaseJPanel {
    public abstract void setPixel(Point2D coordinates, int rgb);
    public abstract  int getPixel(int x, int y);
    public abstract void drawCartesianPlane();
}
