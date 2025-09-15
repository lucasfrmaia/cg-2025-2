package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.points.Point2D;
import project_cg.transformations2d.Scale;
import view.utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ScaleInputs extends ShapePanel {
    private JTextField scaleX, scaleY;

    private JComboBox<String> comboBoxFigures;

    @Override
    protected void initializeInputs() {
        scaleX = new JTextField(10);
        scaleY = new JTextField(10);

        comboBoxFigures = MainScreenSingleton.getComboBoxGeometriFigures();

        addComboBox("Escolha uma figura", comboBoxFigures);
        addInputField("Escala X:", scaleX);
        addInputField("Escala Y:", scaleY);
    }

    @Override
    protected void onCalculate() {
        double sx = Double.parseDouble(scaleX.getText());
        double sy = Double.parseDouble(scaleY.getText());

        MainScreen mainScreen = MainScreenSingleton.getMainScreen();

        String figureSelected = (String) comboBoxFigures.getSelectedItem();
        BaseFigure figure = mainScreen.geometricFiguresHandler.getFigureByID(figureSelected);

        figure.getVertex(
                point2D -> {
                    Point2D pointScaled = Scale.scalePoint(point2D, sx, sy);
                    point2D.updatePoint(pointScaled);
                }
        );

        mainScreen.updateFigures();
    }
}

