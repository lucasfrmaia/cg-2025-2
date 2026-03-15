package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ApplyQueuedTransformationsInputs extends ShapePanel {

    private JComboBox<String> comboBoxFigures;

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
        comboBoxFigures = MainScreenSingleton.getComboBoxGeometriFigures();
        addComboBox("Escolha uma figura", comboBoxFigures);
    }

    @Override
    protected void onCalculate() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        String figureSelected = (String) comboBoxFigures.getSelectedItem();

        if (figureSelected == null || figureSelected.isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecione uma figura para aplicar as transformacoes.");
            return;
        }

        BaseFigure figure = mainScreen.geometricFiguresHandler.getFigureByID(figureSelected);
        QueuedTransformationsPlane plane = getQueuedPlane(mainScreen);

        try {
            plane.applyQueuedTransformations(figure);
            mainScreen.updateFigures();
            JOptionPane.showMessageDialog(this, "Transformacoes acumuladas aplicadas com sucesso.");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private QueuedTransformationsPlane getQueuedPlane(MainScreen mainScreen) {
        return (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");
    }
}
