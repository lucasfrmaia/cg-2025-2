package project_cg.drivers.tudo3D.transformations3dinputs;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ApplyQueuedTransformations3DInputs extends ShapePanel {

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Aplicar Transformacoes 3D";
    }

    @Override
    protected void initializeInputs() {
        JTextField hintField = new JTextField("Aplica todas as transformacoes acumuladas no cubo.", 34);
        hintField.setEditable(false);
        addInputField("Acao:", hintField);
    }

    @Override
    protected void onCalculate() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

        try {
            plane3D.applyQueuedTransformations();
            JOptionPane.showMessageDialog(this, "Transformacoes 3D acumuladas aplicadas com sucesso.");
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
