package project_cg.inputsPanel.recorteInputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.RecorteSutherlandPlane;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ApplyAlgorithmInputs extends ShapePanel {

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected void initializeInputs() {
        JTextField hintField = new JTextField("Recorta as linhas geradas na viewport atual.", 26);
        hintField.setEditable(false);
        addInputField("Acao:", hintField);
    }

    @Override
    protected void onCalculate() {
        RecorteSutherlandPlane plane = getRecortePlane();

        if (plane.getOriginalLineCount() == 0) {
            JOptionPane.showMessageDialog(this, "Gere linhas antes de aplicar o recorte.");
            return;
        }

        plane.applyClipping();

        JOptionPane.showMessageDialog(
                this,
                "Recorte aplicado: " + plane.getClippedLineCount() + " linha(s) visivel(is) na viewport."
        );
    }

    private RecorteSutherlandPlane getRecortePlane() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        return (RecorteSutherlandPlane) mainScreen.JPanelHandler
                .getPanelByCategory("Recorte de Janela de Cohen-Sutherland");
    }
}
