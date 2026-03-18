package project_cg.inputsPanel.recorteInputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.LineClippingPlane;
import utils.BaseJPanel;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class ClipVisualizationModeInput extends ShapePanel {

    private JComboBox<String> modeComboBox;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Aplicar modo";
    }

    @Override
    protected void initializeInputs() {
        modeComboBox = new JComboBox<>(new String[]{
                "Mostrar recorte (linhas verdes)",
                "Esconder parte recortada"
        });

        addComboBox("Visualizacao:", modeComboBox);
    }

    @Override
    protected void onCalculate() {
        LineClippingPlane plane = getRecortePlane();

        String selectedMode = (String) modeComboBox.getSelectedItem();
        boolean hideClippedSegments = "Esconder parte recortada".equals(selectedMode);

        plane.setHideClippedSegments(hideClippedSegments);

        if (hideClippedSegments) {
            JOptionPane.showMessageDialog(this, "Modo aplicado: parte recortada desaparece apos aplicar o algoritmo.");
        } else {
            JOptionPane.showMessageDialog(this, "Modo aplicado: resultado recortado destacado em verde.");
        }
    }

    private LineClippingPlane getRecortePlane() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        String currentCategory = mainScreen.JPanelHandler.getCurrentCategory();
        BaseJPanel panel = mainScreen.JPanelHandler.getPanelByCategory(currentCategory);

        if (panel instanceof LineClippingPlane) {
            return (LineClippingPlane) panel;
        }

        throw new IllegalStateException("A categoria atual nao suporta recorte de linhas.");
    }
}
