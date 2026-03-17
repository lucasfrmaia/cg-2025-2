package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import project_cg.transformations2d.Shear;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ShearInputs extends ShapePanel {
    private JComboBox<String> shearTypeComboBox;
    private JTextField inputA, inputB;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Cisalhamento";
    }

    @Override
    protected void initializeInputs() {
        shearTypeComboBox = new JComboBox<>(new String[]{"X", "Y", "XY"});

        addComboBox("Tipo de Cisalhamento:", shearTypeComboBox);

        inputA = new JTextField(10);
        inputB = new JTextField(10);

        addInputField("Valor de a:", inputA);
        addInputField("Valor de b:", inputB);
    }

    @Override
    protected void onCalculate() {
        try {
            double a = Double.parseDouble(inputA.getText().trim());
            double b = Double.parseDouble(inputB.getText().trim());

            String shearType = (String) shearTypeComboBox.getSelectedItem();

            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            BaseFigure figure = getSingleTransformationFigure(mainScreen);

            if (figure == null) {
                JOptionPane.showMessageDialog(this, "Desenhe o quadrado de referencia antes de adicionar o cisalhamento.");
                return;
            }

            QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");

            if ("X".equals(shearType)) {
                plane.queueTransformation(figure.getID(), point -> Shear.shearX(point, a));
            } else if ("Y".equals(shearType)) {
                plane.queueTransformation(figure.getID(), point -> Shear.shearY(point, b));
            } else {
                plane.queueTransformation(figure.getID(), point -> Shear.shearXY(point, a, b));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite valores validos para os parametros de cisalhamento.");
        }
    }

    private BaseFigure getSingleTransformationFigure(MainScreen mainScreen) {
        if (mainScreen.geometricFiguresHandler.getFigures().isEmpty()) {
            return null;
        }

        return mainScreen.geometricFiguresHandler.getFigures().get(0);
    }

}
