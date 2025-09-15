package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.points.Point2D;
import project_cg.transformations2d.Rotation;
import view.utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class RotationInputs extends ShapePanel {
    private JTextField angleInput;

    private JComboBox<String> comboBoxFigures;

    @Override
    protected void initializeInputs() {
        angleInput = new JTextField(10);
        comboBoxFigures = MainScreenSingleton.getComboBoxGeometriFigures();

        addComboBox("Escolha uma figura", comboBoxFigures);
        addInputField("Ângulo de Rotação:", angleInput);
    }

    @Override
    protected void onCalculate() {
        double angle = Double.parseDouble(angleInput.getText());

        MainScreen mainScreen = MainScreenSingleton.getMainScreen();

        String figureSelected = (String) comboBoxFigures.getSelectedItem();
        BaseFigure figure = mainScreen.geometricFiguresHandler.getFigureByID(figureSelected);

        figure.getVertex(
                point2D -> {
                    Point2D pointRotated = Rotation.rotatePoint(point2D, angle);
                    point2D.updatePoint(pointRotated);
                }
        );

        mainScreen.updateFigures();
    }
}

