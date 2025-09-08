package project_cg.inputsPanel.primitivesInputs;

import project_cg.geometry.figures.Line;
import project_cg.geometry.points.Point2D;
import project_cg.primitives.MidpointLine;
import project_cg.primitives.bases.BaseLine;
import view.utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class MidpointLineInputs extends ShapePanel {
    private JTextField inputP1;
    private JTextField inputP2;

    @Override
    protected void initializeInputs() {
        inputP1 = new JTextField(10);
        inputP1.setToolTipText("Digite o ponto P1");

        inputP2 = new JTextField(10);
        inputP2.setToolTipText("Digite o ponto P2");

        addInputField("Ponto P1:", inputP1);
        addInputField("Ponto P2:", inputP2);
    }

    @Override
    protected void onCalculate() {
        try {
            String[] point1 = inputP1.getText().split(" ");
            String[] point2 = inputP2.getText().split(" ");

            int x1 = Integer.parseInt(point1[0]);
            int y1 = Integer.parseInt(point1[1]);
            int x2 = Integer.parseInt(point2[0]);
            int y2 = Integer.parseInt(point2[1]);

            MainScreen mainScreen = MainScreenSingleton.getMainScreen();

            BaseLine ddaLine = new MidpointLine();

            Line line = new Line(
                    new Point2D(x1, y1),
                    new Point2D(x2, y2),
                    ddaLine
            );

            mainScreen.geometricFiguresHandler.addFigure(line);
            mainScreen.updateFigures();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores válidos para P1 e P2.");
        }

    }


}

