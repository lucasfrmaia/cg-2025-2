package project_cg.drivers.tudo3D.transformations3dinputs;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import project_cg.drivers.tudo3D.transformations3d.Rotation3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import view.utils.ShapePanel;

import javax.swing.*;
import java.util.Objects;
import java.util.function.BiFunction;

public class Rotation3DInputs extends ShapePanel {
    private JComboBox<String> rotationAxisComboBox;
    private JTextField angleInput;
    public Rotation3DInputs() {}

    @Override
    protected void initializeInputs() {
        // Opções de eixos de rotação
        rotationAxisComboBox = new JComboBox<>(new String[]{"X", "Y", "Z"});
        angleInput = new JTextField(10);

        addComboBox("Eixo de rotação:", rotationAxisComboBox);
        addInputField("Ângulo de rotação (em graus):", angleInput);
    }

    @Override
    protected void onCalculate() {
        try {
            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

            String axis = (String) rotationAxisComboBox.getSelectedItem();
            double angle = Double.parseDouble(angleInput.getText());

            // Seleciona a função de rotação com base no eixo escolhido
            BiFunction<Point3D, Double, Point3D> rotationFunction = switch (Objects.requireNonNull(axis)) {
                case "X" -> Rotation3D::rotateX;
                case "Y" -> Rotation3D::rotateY;
                case "Z" -> Rotation3D::rotateZ;
                default -> null;
            };

            if (rotationFunction != null) {
                // Obtém os vértices do cubo
                Point3D[] vertices = plane3D.getCubeVertices();

                if (vertices != null && vertices.length == 8) {
                    // Aplica a rotação a cada vértice no array
                    for (int i = 0; i < vertices.length; i++) {
                        vertices[i] = rotationFunction.apply(vertices[i], angle);
                    }

                    // Atualiza os vértices no plano cartesiano 3D
                    plane3D.setCubeVertices(vertices);

                    // Reinicia a renderização do plano para refletir as mudanças
                    new Thread(() -> plane3D.update(vertices)).start();
                } else {
                    JOptionPane.showMessageDialog(this, "Vértices inválidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Eixo de rotação inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ângulo inválido. Insira um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
