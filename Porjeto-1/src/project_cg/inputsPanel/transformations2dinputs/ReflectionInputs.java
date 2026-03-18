package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.figures.Square;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import project_cg.transformations2d.Reflection;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import java.util.Objects;

public class ReflectionInputs extends ShapePanel {
    private JComboBox<String> reflectionTypeComboBox;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Reflexão";
    }


    @Override
    protected void initializeInputs() {
        reflectionTypeComboBox = new JComboBox<>(new String[]{"X", "Y", "Origem"});
        addComboBox("Tipo de Reflexão:", reflectionTypeComboBox);
    }

    @Override
    protected void onCalculate() {
        String reflectionType = (String) reflectionTypeComboBox.getSelectedItem();

        MainScreen mainScreen = MainScreenSingleton.getMainScreen();

        double[][] reflectionMatrix = switch (Objects.requireNonNull(reflectionType)) {
            case "X" -> Reflection.getReflectionMatrixInX();
            case "Y" -> Reflection.getReflectionMatrixInY();
            case "Origem" -> Reflection.getReflectionMatrixInOrigin();
            default -> null;
        };

        Square square = getSingleTransformationSquare(mainScreen);

        if (square == null) {
            JOptionPane.showMessageDialog(this, "Desenhe o quadrado de referencia antes de adicionar a reflexao.");
            return;
        }

        QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");

        if (reflectionMatrix == null) {
            JOptionPane.showMessageDialog(this, "Tipo de reflexao invalido.");
            return;
        }

        plane.queueTransformation(reflectionMatrix);
    }

    private Square getSingleTransformationSquare(MainScreen mainScreen) {
        if (mainScreen.geometricFiguresHandler.getFigures().size() != 1) {
            return null;
        }

        BaseFigure figure = mainScreen.geometricFiguresHandler.getFigures().get(0);
        if (!(figure instanceof Square)) {
            return null;
        }

        return (Square) figure;
    }
}


