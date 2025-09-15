package project_cg.inputsPanel.primitivesInputs;

import project_cg.geometry.figures.Ellipse;
import project_cg.primitives.MidpointElipse;
import view.utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class MidpointElipseInputs extends ShapePanel {
    private JTextField cxField;
    private JTextField cyField;
    private JTextField aField;
    private JTextField bField;

    @Override
    protected void initializeInputs() {
        cxField = new JTextField(10);
        cyField = new JTextField(10);
        aField = new JTextField(10);
        bField = new JTextField(10);

        addInputField("Eixo A:", aField);
        addInputField("Eixo B:", bField);
    }

    @Override
    protected void onCalculate() {
        int a = Integer.parseInt(aField.getText());
        int b = Integer.parseInt(bField.getText());

        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        MidpointElipse midPointElipse = new MidpointElipse();

        Ellipse ellipse = new Ellipse(a, b, midPointElipse);

        mainScreen.geometricFiguresHandler.addFigure(ellipse);
        mainScreen.updateFigures();
    }

}

