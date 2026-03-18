package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.figures.Square;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ApplyQueuedTransformationsInputs extends ShapePanel {

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Aplicar Transformações";
    }

    @Override
    protected void initializeInputs() {
    }

    @Override
    protected void onCalculate() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        Square square = getSingleTransformationSquare(mainScreen);

        if (square == null) {
            JOptionPane.showMessageDialog(this, "Desenhe o quadrado de referencia antes de aplicar as transformacoes.");
            return;
        }

        QueuedTransformationsPlane plane = getQueuedPlane(mainScreen);

        try {
            plane.applyQueuedTransformations(square);
            mainScreen.updateFigures();
            JOptionPane.showMessageDialog(this, "Transformacoes acumuladas aplicadas com sucesso.");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
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

    private QueuedTransformationsPlane getQueuedPlane(MainScreen mainScreen) {
        return (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");
    }
}
