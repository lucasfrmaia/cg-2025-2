package project_cg.inputsPanel.transformations3dinputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport3d.CartesianPlane3D;
import project_cg.geometry.points.Point3D;
import project_cg.transformations3d.Reflection3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import utils.ShapePanel;

import javax.swing.*;
import java.util.Objects;
import java.util.function.Function;

public class Reflection3DInputs extends ShapePanel {
    private JComboBox<String> reflectionTypeComboBox;

    public Reflection3DInputs() {}

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Reflexao 3D";
    }

    @Override
    protected void initializeInputs() {
        // Tipos de reflexao em 3D
        reflectionTypeComboBox = new JComboBox<>(new String[]{"XY", "XZ", "YZ", "Origem"});
        addComboBox("Tipo de Reflexao:", reflectionTypeComboBox);
    }

    @Override
    protected void onCalculate() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

        String reflectionType = (String) reflectionTypeComboBox.getSelectedItem();

        // Seleciona a funcao de reflexao com base na escolha do usuario
        Function<Point3D, Point3D> reflectionFunction = switch (Objects.requireNonNull(reflectionType)) {
            case "XY" -> Reflection3D::reflectInXY;
            case "XZ" -> Reflection3D::reflectInXZ;
            case "YZ" -> Reflection3D::reflectInYZ;
            case "Origem" -> Reflection3D::reflectInOrigin;
            default -> null;
        };

        if (reflectionFunction != null) {
            Point3D[] vertices = plane3D.getCubeVertices();

            if (vertices == null || vertices.length != 8) {
                JOptionPane.showMessageDialog(this, "Vertices invalidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            plane3D.queueTransformation(reflectionFunction::apply);
        } else {
            JOptionPane.showMessageDialog(this, "Tipo de reflexao invalido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
