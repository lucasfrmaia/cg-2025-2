package project_cg.drivers.tudo3D.transformations3dinputs;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import project_cg.drivers.tudo3D.transformations3d.Reflection3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import view.utils.ShapePanel;

import javax.swing.*;
import java.util.Objects;
import java.util.function.Function;

public class Reflection3DInputs extends ShapePanel {
    private JComboBox<String> reflectionTypeComboBox;
    private CartesianPlane3D plane3D;

    public Reflection3DInputs() {}

    @Override
    protected void initializeInputs() {
        // Tipos de reflexão em 3D
        reflectionTypeComboBox = new JComboBox<>(new String[]{"XY", "XZ", "YZ", "Origem"});
        addComboBox("Tipo de Reflexão:", reflectionTypeComboBox);
    }

    @Override
    protected void onCalculate() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

        String reflectionType = (String) reflectionTypeComboBox.getSelectedItem();

        // Seleciona a função de reflexão com base na escolha do usuário
        Function<Point3D, Point3D> reflectionFunction = switch (Objects.requireNonNull(reflectionType)) {
            case "XY" -> Reflection3D::reflectInXY;
            case "XZ" -> Reflection3D::reflectInXZ;
            case "YZ" -> Reflection3D::reflectInYZ;
            case "Origem" -> Reflection3D::reflectInOrigin;
            default -> null;
        };

        if (reflectionFunction != null) {
            // Obtém os vértices do cubo
            Point3D[] vertices = plane3D.getCubeVertices();

            if (vertices != null && vertices.length == 8) {
                // Aplica a reflexão a cada vértice no array
                for (int i = 0; i < vertices.length; i++) {
                    vertices[i] = reflectionFunction.apply(vertices[i]);
                }

                // Atualiza os vértices do plano cartesiano 3D
                plane3D.setCubeVertices(vertices);

                // Reinicia a renderização do plano para refletir as mudanças
                new Thread(() -> plane3D.update(vertices)).start();
            } else {
                JOptionPane.showMessageDialog(this, "Vértices inválidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tipo de reflexão inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
