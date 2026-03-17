package project_cg.drivers.tudo3D.transformations3dinputs;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import project_cg.drivers.tudo3D.transformations3d.Shear3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import utils.ShapePanel;

import javax.swing.*;
import java.util.Objects;

public class Shear3DInputs extends ShapePanel {
    private JComboBox<String> shearAxisComboBox;
    private JTextField shearFactor1Input;
    private JTextField shearFactor2Input;

    public Shear3DInputs() {}

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Cisalhamento 3D";
    }

    @Override
    protected void initializeInputs() {
        // Opcoes de eixos para cisalhamento
        shearAxisComboBox = new JComboBox<>(new String[]{"X (Y, Z)", "Y (X, Z)", "Z (X, Y)"});
        shearFactor1Input = new JTextField(10);
        shearFactor2Input = new JTextField(10);

        addComboBox("Eixo de cisalhamento:", shearAxisComboBox);
        addInputField("Fator de cisalhamento 1:", shearFactor1Input);
        addInputField("Fator de cisalhamento 2:", shearFactor2Input);
    }

    @Override
    protected void onCalculate() {
        try {
            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

            String axis = (String) shearAxisComboBox.getSelectedItem();
            double shearFactor1 = Double.parseDouble(shearFactor1Input.getText());
            double shearFactor2 = Double.parseDouble(shearFactor2Input.getText());

            // Seleciona a funcao de cisalhamento com base no eixo escolhido
            ShearFunction shearFunction = switch (Objects.requireNonNull(axis)) {
                case "X (Y, Z)" -> Shear3D::shearX;
                case "Y (X, Z)" -> Shear3D::shearY;
                case "Z (X, Y)" -> Shear3D::shearZ;
                default -> null;
            };

            if (shearFunction != null) {
                // Obtem os vertices do cubo
                Point3D[] vertices = plane3D.getCubeVertices();

                if (vertices == null || vertices.length != 8) {
                    JOptionPane.showMessageDialog(this, "Vertices invalidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                plane3D.queueTransformation(point -> shearFunction.apply(point, shearFactor1, shearFactor2));
            } else {
                JOptionPane.showMessageDialog(this, "Eixo de cisalhamento invalido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Fator de cisalhamento invalido. Insira valores numericos validos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FunctionalInterface
    private interface ShearFunction {
        Point3D apply(Point3D point, double factor1, double factor2);
    }
}
