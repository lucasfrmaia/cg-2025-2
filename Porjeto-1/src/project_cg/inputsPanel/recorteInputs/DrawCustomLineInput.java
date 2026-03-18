package project_cg.inputsPanel.recorteInputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.LineClippingPlane;
import project_cg.geometry.points.Point2D;
import utils.BaseJPanel;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class DrawCustomLineInput extends ShapePanel {

    private JTextField p1Field;
    private JTextField p2Field;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Desenhar";
    }

    @Override
    protected void initializeInputs() {
        p1Field = new JTextField(12);
        p2Field = new JTextField(12);

        p1Field.setToolTipText("Exemplo: 10 10");
        p2Field.setToolTipText("Exemplo: 240 180");

        addInputField("P1 (x y):", p1Field);
        addInputField("P2 (x y):", p2Field);
    }

    @Override
    protected void onCalculate() {
        try {
            Point2D p1 = parsePoint(p1Field.getText());
            Point2D p2 = parsePoint(p2Field.getText());

            LineClippingPlane plane = getRecortePlane();
            plane.addCustomLine(p1, p2);

            JOptionPane.showMessageDialog(this, "Reta customizada adicionada ao recorte.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private Point2D parsePoint(String rawPoint) {
        String[] parts = rawPoint.trim().split("\\s+");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Informe o ponto no formato: x y");
        }

        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return new Point2D(x, y);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Os valores de x e y devem ser inteiros.");
        }
    }

    private LineClippingPlane getRecortePlane() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        String currentCategory = mainScreen.JPanelHandler.getCurrentCategory();
        BaseJPanel panel = mainScreen.JPanelHandler.getPanelByCategory(currentCategory);

        if (panel instanceof LineClippingPlane) {
            return (LineClippingPlane) panel;
        }

        throw new IllegalStateException("A categoria atual nao suporta recorte de linhas.");
    }
}

