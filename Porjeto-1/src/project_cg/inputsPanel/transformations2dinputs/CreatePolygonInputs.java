package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.Square;
import project_cg.geometry.points.Point2D;
import project_cg.primitives.MidpointLine;
import view.mainScreen.MainScreen;
import view.utils.ShapePanel;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;
import java.util.Arrays;

public class CreatePolygonInputs extends ShapePanel {

    private JTextField pointsInput1;
    private JTextField pointsInput2;
    private JTextField pointsInput3;
    private JTextField pointsInput4;

    @Override
    protected void initializeInputs() {
        pointsInput1 = new JTextField(30);
        pointsInput2 = new JTextField(30);
        pointsInput3 = new JTextField(30);
        pointsInput4 = new JTextField(30);

        addInputField("Digite as coordenadas (x, y) do Ponto 1:", pointsInput1);
        addInputField("Digite as coordenadas (x, y) do Ponto 2", pointsInput2);
        addInputField("Digite as coordenadas (x, y) do Ponto 3", pointsInput3);
        addInputField("Digite as coordenadas (x, y) do Ponto 4", pointsInput4);
    }

    @Override
    protected void onCalculate() {
        double[] points1 = Arrays.stream(pointsInput1.getText().split(" "))
                .map(Double::parseDouble)
                .toList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        double[] points2 = Arrays.stream(pointsInput2.getText().split(" "))
                .map(Double::parseDouble)
                .toList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        double[] points3 = Arrays.stream(pointsInput3.getText().split(" "))
                .map(Double::parseDouble)
                .toList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        double[] points4 = Arrays.stream(pointsInput4.getText().split(" "))
                .map(Double::parseDouble)
                .toList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        MainScreen mainScreen = MainScreenSingleton.getMainScreen();

        MidpointLine midpointLine = new MidpointLine();

        Square square = new Square(
            new Point2D[] {
                new Point2D(points1[0], points1[1]),
                new Point2D(points2[0], points2[1]),
                new Point2D(points3[0], points3[1]),
                new Point2D(points4[0], points4[1])
            },
            midpointLine
        );

       mainScreen.geometricFiguresHandler.addFigure(square);
       mainScreen.updateFigures();

    }


}
