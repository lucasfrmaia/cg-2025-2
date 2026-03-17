package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import project_cg.transformations2d.Scale;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ScaleInputs extends ShapePanel {
    private JTextField scaleX, scaleY;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Escala";
    }

    @Override
    protected void initializeInputs() {
        scaleX = new JTextField(10);
        scaleY = new JTextField(10);
        addInputField("Escala X:", scaleX);
        addInputField("Escala Y:", scaleY);
    }

    @Override
    protected void onCalculate() {
        try {
            double sx = Double.parseDouble(scaleX.getText().trim());
            double sy = Double.parseDouble(scaleY.getText().trim());

            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            BaseFigure figure = getSingleTransformationFigure(mainScreen);

            if (figure == null) {
                JOptionPane.showMessageDialog(this, "Desenhe o quadrado de referencia antes de adicionar a escala.");
                return;
            }

            QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");
            plane.queueTransformation(figure.getID(), point -> Scale.scalePoint(point, sx, sy));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite valores validos para escala X e Y.");
        }
    }

    private BaseFigure getSingleTransformationFigure(MainScreen mainScreen) {
        if (mainScreen.geometricFiguresHandler.getFigures().isEmpty()) {
            return null;
        }

        return mainScreen.geometricFiguresHandler.getFigures().get(0);
    }
}

