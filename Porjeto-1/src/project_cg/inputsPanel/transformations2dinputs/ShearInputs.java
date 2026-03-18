package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.figures.Square;
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
            Square square = getSingleTransformationSquare(mainScreen);

            if (square == null) {
                JOptionPane.showMessageDialog(this, "Desenhe o quadrado de referencia antes de adicionar o cisalhamento.");
                return;
            }

            QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");

            if ("X".equals(shearType)) {
                plane.queueTransformation(new Shear(Shear.Type.IN_X, a, 0));
            } else if ("Y".equals(shearType)) {
                plane.queueTransformation(new Shear(Shear.Type.IN_Y, 0, b));
            } else {
                plane.queueTransformation(new Shear(Shear.Type.IN_XY, a, b));
            }

            view.MainScreenV2.refreshQueuedTransformationsIndicator();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite valores validos para os parametros de cisalhamento.");
        }
    }

    private Square getSingleTransformationSquare(MainScreen mainScreen) {
        if (mainScreen.geometricFiguresHandler.getFigures().size() != 1) {
            return null;
        }

        BaseFigure figure = mainScreen.geometricFiguresHandler.getFigures().get(0);
        if (!(figure instanceof Square)) {
            return null;
        }

        return (Square) figure;
    }

}
