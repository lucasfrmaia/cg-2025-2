package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.points.Point2D;
import project_cg.transformations2d.Shear;
import view.utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ShearInputs extends ShapePanel {
    private JComboBox<String> shearTypeComboBox;
    private JComboBox<String> comboBoxFigures;
    private JTextField inputA, inputB;

    @Override
    protected void initializeInputs() {
        shearTypeComboBox = new JComboBox<>(new String[]{"X", "Y", "XY"});
        comboBoxFigures = MainScreenSingleton.getComboBoxGeometriFigures();

        addComboBox("Escolha uma figura", comboBoxFigures);
        addComboBox("Tipo de Cisalhamento:", shearTypeComboBox);

        inputA = new JTextField(10);
        inputB = new JTextField(10);

        addInputField("Valor de a:", inputA);
        addInputField("Valor de b:", inputB);
    }

    @Override
    protected void onCalculate() {

        double a = Double.parseDouble(inputA.getText());
        double b = Double.parseDouble(inputB.getText());

        String shearType = (String) shearTypeComboBox.getSelectedItem();

        MainScreen mainScreen = MainScreenSingleton.getMainScreen();

        String squareSelected = (String) comboBoxFigures.getSelectedItem();
        BaseFigure figure = mainScreen.geometricFiguresHandler.getFigureByID(squareSelected);

        figure.getVertex(
                point2D -> {
                    assert shearType != null;

                    Point2D pointReflected;

                    if (shearType.equals("X")) {
                       pointReflected = Shear.shearX(point2D, a);
                    } else if (shearType.equals("Y")) {
                        pointReflected = Shear.shearY(point2D, b);
                    } else {
                        pointReflected = Shear.shearXY(point2D, a, b);
                    }

                    point2D.updatePoint(pointReflected);
                }
        );

        mainScreen.updateFigures();
    }

}
