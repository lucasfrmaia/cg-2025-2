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

    private JComboBox<String> comboBoxFigures;

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
        comboBoxFigures = MainScreenSingleton.getComboBoxGeometriFigures();

        addComboBox("Escolha uma figura", comboBoxFigures);
        addInputField("Ângulo de Rotação:", angleInput);
    }

    @Override
    protected void onCalculate() {
        try {
            double angle = Double.parseDouble(angleInput.getText().trim());
            MainScreen mainScreen = MainScreenSingleton.getMainScreen();

            String figureSelected = (String) comboBoxFigures.getSelectedItem();
            BaseFigure figure = mainScreen.geometricFiguresHandler.getFigureByID(figureSelected);

            if (figure == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma figura valida para adicionar a rotacao.");
                return;
            }

            QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");
            plane.queueTransformation(figure.getID(), point -> Rotation.rotatePoint(point, angle));

            JOptionPane.showMessageDialog(
                    this,
                    "Rotacao adicionada. Total pendente para a figura: " + plane.getPendingCount(figure.getID())
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um angulo valido.");
        }
    }
}

