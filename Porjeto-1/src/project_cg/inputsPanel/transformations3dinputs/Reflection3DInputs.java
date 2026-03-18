package project_cg.inputsPanel.transformations3dinputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport3d.CartesianPlane3D;
import project_cg.geometry.points.Point3D;
import project_cg.transformations3d.Reflection3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import utils.ShapePanel;

import javax.swing.*;
import java.util.Objects;

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

        Reflection3D reflection = switch (Objects.requireNonNull(reflectionType)) {
            case "XY" -> new Reflection3D(Reflection3D.Type.IN_XY);
            case "XZ" -> new Reflection3D(Reflection3D.Type.IN_XZ);
            case "YZ" -> new Reflection3D(Reflection3D.Type.IN_YZ);
            case "Origem" -> new Reflection3D(Reflection3D.Type.IN_ORIGIN);
            default -> null;
        };

        if (reflection != null) {
            Point3D[] vertices = plane3D.getCubeVertices();

            if (vertices == null || vertices.length != 8) {
                JOptionPane.showMessageDialog(this, "Vertices invalidos ou ausentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            plane3D.queueTransformation(reflection);
            view.MainScreenV2.refreshQueuedTransformationsIndicator();
        } else {
            JOptionPane.showMessageDialog(this, "Tipo de reflexao invalido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
