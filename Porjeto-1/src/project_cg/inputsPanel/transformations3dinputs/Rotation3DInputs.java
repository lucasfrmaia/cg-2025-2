package project_cg.inputsPanel.transformations3dinputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport3d.CartesianPlane3D;
import project_cg.geometry.points.Point3D;
import project_cg.transformations3d.Rotation3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import utils.ShapePanel;

import javax.swing.*;
import java.util.Objects;

public class Rotation3DInputs extends ShapePanel {
    private JComboBox<String> rotationAxisComboBox;
    private JTextField angleInput;
    public Rotation3DInputs() {}

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Rotacao 3D";
    }

    @Override
    protected void initializeInputs() {
        // Opcoes de eixos de rotacao
        rotationAxisComboBox = new JComboBox<>(new String[]{"X", "Y", "Z"});
        angleInput = new JTextField(10);

        addComboBox("Eixo de rotacao:", rotationAxisComboBox);
        addInputField("Angulo de rotacao (em graus):", angleInput);
    }

    @Override
    protected void onCalculate() {
        try {
            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

            String axis = (String) rotationAxisComboBox.getSelectedItem();
            double angle = Double.parseDouble(angleInput.getText());

            Rotation3D rotation = switch (Objects.requireNonNull(axis)) {
                case "X" -> new Rotation3D(Rotation3D.Axis.X, angle);
                case "Y" -> new Rotation3D(Rotation3D.Axis.Y, angle);
                case "Z" -> new Rotation3D(Rotation3D.Axis.Z, angle);
                default -> null;
            };

            if (rotation != null) {
                Point3D[] vertices = plane3D.getCubeVertices();
                if (vertices == null || vertices.length != 8) {
                    JOptionPane.showMessageDialog(this, "Vertices invalidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                plane3D.queueTransformation(rotation);
                view.MainScreenV2.refreshQueuedTransformationsIndicator();
            } else {
                JOptionPane.showMessageDialog(this, "Eixo de rotacao invalido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Angulo invalido. Insira um numero valido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
