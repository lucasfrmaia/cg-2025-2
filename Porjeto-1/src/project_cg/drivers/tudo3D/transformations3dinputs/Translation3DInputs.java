package project_cg.drivers.tudo3D.transformations3dinputs;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import project_cg.drivers.tudo3D.transformations3d.Translation3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import view.utils.ShapePanel;

import javax.swing.*;

public class Translation3DInputs extends ShapePanel {
    private JTextField translateXInput;
    private JTextField translateYInput;
    private JTextField translateZInput;

    public Translation3DInputs() {}

    @Override
    protected void initializeInputs() {
        // Campos de entrada para os valores de translação em X, Y e Z
        translateXInput = new JTextField(10);
        translateYInput = new JTextField(10);
        translateZInput = new JTextField(10);

        addInputField("Valor de translação em X:", translateXInput);
        addInputField("Valor de translação em Y:", translateYInput);
        addInputField("Valor de translação em Z:", translateZInput);
    }

    @Override
    protected void onCalculate() {
        try {
            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

            // Obtém os valores de translação fornecidos pelo usuário
            double tx = Double.parseDouble(translateXInput.getText());
            double ty = Double.parseDouble(translateYInput.getText());
            double tz = Double.parseDouble(translateZInput.getText());

            // Obtém os vértices do cubo
            Point3D[] vertices = plane3D.getCubeVertices();

            if (vertices != null && vertices.length == 8) {
                // Aplica a translação a cada vértice no array
                for (int i = 0; i < vertices.length; i++) {
                    vertices[i] = Translation3D.translatePoint(vertices[i], tx, ty, tz);
                }

                // Atualiza os vértices no plano cartesiano 3D
                plane3D.setCubeVertices(vertices);

                // Reinicia a renderização do plano para refletir as mudanças
                new Thread(() -> plane3D.update(vertices)).start();
            } else {
                JOptionPane.showMessageDialog(this, "Vértices inválidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor de translação inválido. Insira valores numéricos válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
