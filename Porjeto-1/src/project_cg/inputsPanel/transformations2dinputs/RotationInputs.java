package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import project_cg.transformations2d.Rotation;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class RotationInputs extends ShapePanel {
    private JTextField angleInput;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Rotação";
    }

    @Override
    protected void initializeInputs() {
        angleInput = new JTextField(10);
        addInputField("Ângulo de Rotação:", angleInput);
    }

    @Override
    protected void onCalculate() {
        try {
            double angle = Double.parseDouble(angleInput.getText().trim());
            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            BaseFigure figure = getSingleTransformationFigure(mainScreen);

            if (figure == null) {
                JOptionPane.showMessageDialog(this, "Desenhe o quadrado de referencia antes de adicionar a rotacao.");
                return;
            }

            QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");
            plane.queueTransformation(figure.getID(), point -> Rotation.rotatePoint(point, angle));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um angulo valido.");
        }
    }

    private BaseFigure getSingleTransformationFigure(MainScreen mainScreen) {
        if (mainScreen.geometricFiguresHandler.getFigures().isEmpty()) {
            return null;
        }

        return mainScreen.geometricFiguresHandler.getFigures().get(0);
    }
}

