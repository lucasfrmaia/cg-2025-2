package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import project_cg.transformations2d.Translation;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class TranslationInputs extends ShapePanel {
    private JTextField translationX, translationY;

    private JComboBox<String> comboBoxFigures;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Translação";
    }

    @Override
    protected void initializeInputs() {
        translationX = new JTextField(10);
        translationY = new JTextField(10);
        comboBoxFigures = MainScreenSingleton.getComboBoxGeometriFigures();

        addComboBox("Escolha uma figura", comboBoxFigures);
        addInputField("Translação X:", translationX);
        addInputField("Translação Y:", translationY);
    }

    @Override
    protected void onCalculate() {
        try {
            int tx = Integer.parseInt(translationX.getText().trim());
            int ty = Integer.parseInt(translationY.getText().trim());

            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            String figureSelected = (String) comboBoxFigures.getSelectedItem();
            BaseFigure figure = mainScreen.geometricFiguresHandler.getFigureByID(figureSelected);

            if (figure == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma figura valida para adicionar a translacao.");
                return;
            }

            QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");
            plane.queueTransformation(figure.getID(), point -> Translation.translatePoint(point, tx, ty));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite valores inteiros validos para translacao X e Y.");
        }
    }

}
