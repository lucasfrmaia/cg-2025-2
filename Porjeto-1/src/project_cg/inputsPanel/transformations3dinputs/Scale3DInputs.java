package project_cg.inputsPanel.transformations3dinputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport3d.CartesianPlane3D;
import project_cg.geometry.points.Point3D;
import project_cg.transformations3d.Scale3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import utils.ShapePanel;

import javax.swing.*;

public class Scale3DInputs extends ShapePanel {
    private JTextField scaleXInput;
    private JTextField scaleYInput;
    private JTextField scaleZInput;

    public Scale3DInputs() {}

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Escala 3D";
    }

    @Override
    protected void initializeInputs() {
        // Campos de entrada para os fatores de escala em X, Y e Z
        scaleXInput = new JTextField(10);
        scaleYInput = new JTextField(10);
        scaleZInput = new JTextField(10);

        addInputField("Fator de escala em X:", scaleXInput);
        addInputField("Fator de escala em Y:", scaleYInput);
        addInputField("Fator de escala em Z:", scaleZInput);
    }

    @Override
    protected void onCalculate() {
        try {
            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

            // Obtem os fatores de escala fornecidos pelo usuario
            double sx = Double.parseDouble(scaleXInput.getText());
            double sy = Double.parseDouble(scaleYInput.getText());
            double sz = Double.parseDouble(scaleZInput.getText());

            // Obtem os vertices do cubo
            Point3D[] vertices = plane3D.getCubeVertices();

            if (vertices == null || vertices.length != 8) {
                JOptionPane.showMessageDialog(this, "Vertices invalidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            plane3D.queueTransformation(new Scale3D(sx, sy, sz));
            view.MainScreenV2.refreshQueuedTransformationsIndicator();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Fator de escala invalido. Insira valores numericos validos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
