package project_cg.inputsPanel.primitivesInputs;

import project_cg.geometry.bezierAlgorithm.Bezier;
import project_cg.geometry.figures.BezierCurve;
import project_cg.geometry.points.Point2D;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;
import java.util.List;

public class BezierInputs extends ShapePanel {

    private JTextField inputP0;
    private JTextField inputP1;
    private JTextField inputP2;
    private JTextField inputP3;
    private JTextField inputSegments;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Desenhar";
    }

    @Override
    protected void initializeInputs() {
        inputP0 = new JTextField(10);
        inputP1 = new JTextField(10);
        inputP2 = new JTextField(10);
        inputP3 = new JTextField(10);
        inputSegments = new JTextField(10);

        inputP0.setToolTipText("Digite P0 no formato: x y");
        inputP1.setToolTipText("Digite P1 no formato: x y");
        inputP2.setToolTipText("Digite P2 no formato: x y");
        inputP3.setToolTipText("Digite P3 no formato: x y");
        inputSegments.setToolTipText("Quantidade de segmentos da curva");

        addInputField("P0 (x y):", inputP0);
        addInputField("P1 (x y):", inputP1);
        addInputField("P2 (x y):", inputP2);
        addInputField("P3 (x y):", inputP3);
        addInputField("Segmentos:", inputSegments);
    }

    @Override
    protected void onCalculate() {
        try {
            Point2D p0 = parsePoint(inputP0.getText());
            Point2D p1 = parsePoint(inputP1.getText());
            Point2D p2 = parsePoint(inputP2.getText());
            Point2D p3 = parsePoint(inputP3.getText());
            int segments = Integer.parseInt(inputSegments.getText().trim());

            if (segments <= 0) {
                throw new IllegalArgumentException();
            }

            MainScreen mainScreen = MainScreenSingleton.getMainScreen();

            Bezier bezier = new Bezier();
            BezierCurve curve = new BezierCurve(List.of(p0, p1, p2, p3), segments, bezier);

            mainScreen.geometricFiguresHandler.addFigure(curve);
            mainScreen.updateFigures();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Informe pontos no formato 'x y' e segmentos > 0.");
        }
    }

    private Point2D parsePoint(String value) {
        String[] parts = value.trim().split("\\s+");

        if (parts.length != 2) {
            throw new IllegalArgumentException();
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);

        return new Point2D(x, y);
    }
}