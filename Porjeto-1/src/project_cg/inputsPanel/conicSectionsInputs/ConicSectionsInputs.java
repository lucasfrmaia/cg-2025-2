package project_cg.inputsPanel.conicSectionsInputs;

import project_cg.geometry.figures.ConicSection;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ConicSectionsInputs extends ShapePanel {

    private JComboBox<String> typeCombo;
    private JTextField centerXField;
    private JTextField centerYField;
    private JTextField parameterAField;
    private JTextField parameterBField;
    private JTextField rangeField;
    private JTextField samplesField;

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
        typeCombo = new JComboBox<>(new String[] {"Elipse", "Parabola", "Hiperbole"});
        centerXField = new JTextField("0", 8);
        centerYField = new JTextField("0", 8);
        parameterAField = new JTextField("60", 8);
        parameterBField = new JTextField("30", 8);
        rangeField = new JTextField("140", 8);
        samplesField = new JTextField("180", 8);

        parameterAField.setToolTipText("Elipse/Hiperbole: a | Parabola: p");
        parameterBField.setToolTipText("Elipse/Hiperbole: b | Parabola: ignorado");
        rangeField.setToolTipText("Alcance de amostragem para parabola/hiperbole");
        samplesField.setToolTipText("Quantidade de pontos para amostrar");

        addComboBox("Tipo:", typeCombo);
        addInputField("Centro X:", centerXField);
        addInputField("Centro Y:", centerYField);
        addInputField("Parametro A/P:", parameterAField);
        addInputField("Parametro B:", parameterBField);
        addInputField("Alcance:", rangeField);
        addInputField("Amostras:", samplesField);
    }

    @Override
    protected void onCalculate() {
        try {
            ConicSection.ConicType type = parseType((String) typeCombo.getSelectedItem());
            int centerX = Integer.parseInt(centerXField.getText().trim());
            int centerY = Integer.parseInt(centerYField.getText().trim());
            double parameterA = Double.parseDouble(parameterAField.getText().trim());
            double parameterB = Double.parseDouble(parameterBField.getText().trim());
            int range = Integer.parseInt(rangeField.getText().trim());
            int samples = Integer.parseInt(samplesField.getText().trim());

            ConicSection conicSection = new ConicSection(
                    type,
                    centerX,
                    centerY,
                    parameterA,
                    parameterB,
                    range,
                    samples
            );

            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            mainScreen.geometricFiguresHandler.addFigure(conicSection);
            mainScreen.updateFigures();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preencha os campos numericos com valores validos.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private ConicSection.ConicType parseType(String rawType) {
        if ("Elipse".equals(rawType)) {
            return ConicSection.ConicType.ELLIPSE;
        }

        if ("Parabola".equals(rawType)) {
            return ConicSection.ConicType.PARABOLA;
        }

        return ConicSection.ConicType.HYPERBOLA;
    }
}
