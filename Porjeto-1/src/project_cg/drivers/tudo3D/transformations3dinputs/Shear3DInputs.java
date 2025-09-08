package project_cg.drivers.tudo3D.transformations3dinputs;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import project_cg.drivers.tudo3D.transformations3d.Shear3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import view.utils.ShapePanel;

import javax.swing.*;
import java.util.Objects;

public class Shear3DInputs extends ShapePanel {
    private JComboBox<String> shearAxisComboBox;
    private JTextField shearFactor1Input;
    private JTextField shearFactor2Input;

    public Shear3DInputs() {}

    @Override
    protected void initializeInputs() {
        // Opções de eixos para cisalhamento
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

            // Seleciona a função de cisalhamento com base no eixo escolhido
            ShearFunction shearFunction = switch (Objects.requireNonNull(axis)) {
                case "X (Y, Z)" -> Shear3D::shearX;
                case "Y (X, Z)" -> Shear3D::shearY;
                case "Z (X, Y)" -> Shear3D::shearZ;
                default -> null;
            };

            if (shearFunction != null) {
                // Obtém os vértices do cubo
                Point3D[] vertices = plane3D.getCubeVertices();

                if (vertices != null && vertices.length == 8) {
                    // Aplica o cisalhamento a cada vértice no array
                    for (int i = 0; i < vertices.length; i++) {
                        vertices[i] = shearFunction.apply(vertices[i], shearFactor1, shearFactor2);
                    }

                    // Atualiza os vértices no plano cartesiano 3D
                    plane3D.setCubeVertices(vertices);

                    // Reinicia a renderização do plano para refletir as mudanças
                    new Thread(() -> plane3D.update(vertices)).start();

                } else {
                    JOptionPane.showMessageDialog(this, "Vértices inválidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Eixo de cisalhamento inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Fator de cisalhamento inválido. Insira valores numéricos válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FunctionalInterface
    private interface ShearFunction {
        Point3D apply(Point3D point, double factor1, double factor2);
    }
}
