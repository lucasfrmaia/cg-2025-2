package view.utils;

import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane;
import utils.Constants;

import javax.swing.*;

public abstract class BaseJPanel extends JPanel {

    public BaseJPanel() {
        this.setSize(Constants.WIDTH_CARTESIAN_PLANE, Constants.HEIGHT_CARTESIAN_PLANE);
        setBackground(Constants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createLineBorder(Constants.SECONDARY_COLOR, 1));
        setFont(Constants.UI_FONT);
    }

    public void clear() {
        throw new RuntimeException("Método não implementado");
    }
    ;
    public abstract BaseJPanel reset();
}
