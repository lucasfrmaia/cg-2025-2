package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
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
        BaseFigure figure = getSingleTransformationFigure(mainScreen);

        if (figure == null) {
            JOptionPane.showMessageDialog(this, "Desenhe o quadrado de referencia antes de aplicar as transformacoes.");
            return;
        }

        QueuedTransformationsPlane plane = getQueuedPlane(mainScreen);

        try {
            plane.applyQueuedTransformations(figure);
            mainScreen.updateFigures();
            JOptionPane.showMessageDialog(this, "Transformacoes acumuladas aplicadas com sucesso.");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private BaseFigure getSingleTransformationFigure(MainScreen mainScreen) {
        if (mainScreen.geometricFiguresHandler.getFigures().isEmpty()) {
            return null;
        }

        return mainScreen.geometricFiguresHandler.getFigures().get(0);
    }

    private QueuedTransformationsPlane getQueuedPlane(MainScreen mainScreen) {
        return (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");
    }
}
