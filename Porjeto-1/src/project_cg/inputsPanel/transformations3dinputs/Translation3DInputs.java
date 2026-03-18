package project_cg.inputsPanel.transformations3dinputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport3d.CartesianPlane3D;
import project_cg.geometry.points.Point3D;
import project_cg.transformations3d.Translation3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import utils.ShapePanel;

import javax.swing.*;

public class Translation3DInputs extends ShapePanel {
    private JTextField translateXInput;
    private JTextField translateYInput;
    private JTextField translateZInput;

    public Translation3DInputs() {}

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Translacao 3D";
    }

    @Override
    protected void initializeInputs() {
        // Campos de entrada para os valores de translacao em X, Y e Z
        translateXInput = new JTextField(10);
        translateYInput = new JTextField(10);
        translateZInput = new JTextField(10);

        addInputField("Valor de translacao em X:", translateXInput);
        addInputField("Valor de translacao em Y:", translateYInput);
        addInputField("Valor de translacao em Z:", translateZInput);
    }

    @Override
    protected void onCalculate() {
        try {
            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

            // Obtem os valores de translacao fornecidos pelo usuario
            double tx = Double.parseDouble(translateXInput.getText());
            double ty = Double.parseDouble(translateYInput.getText());
            double tz = Double.parseDouble(translateZInput.getText());

            // Obtem os vertices do cubo
            Point3D[] vertices = plane3D.getCubeVertices();

            if (vertices == null || vertices.length != 8) {
                JOptionPane.showMessageDialog(this, "Vertices invalidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            plane3D.queueTransformation(point -> Translation3D.translatePoint(point, tx, ty, tz));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor de translacao invalido. Insira valores numericos validos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
