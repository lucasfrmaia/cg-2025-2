package project_cg.inputsPanel.recorteInputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.RecorteSutherlandPlane;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class DrawLinesInputs extends ShapePanel {

    private JTextField quantityField;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }


    @Override
    protected void initializeInputs() {
        quantityField = new JTextField(8);
        quantityField.setText("12");
        addInputField("Quantidade de linhas:", quantityField);
    }

    @Override
    protected void onCalculate() {
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());

            RecorteSutherlandPlane plane = getRecortePlane();
            plane.generateRandomLines(quantity);

            JOptionPane.showMessageDialog(
                    this,
                    "Foram geradas " + plane.getOriginalLineCount() + " linhas aleatorias."
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite uma quantidade inteira valida.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private RecorteSutherlandPlane getRecortePlane() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        return (RecorteSutherlandPlane) mainScreen.JPanelHandler
                .getPanelByCategory("Recorte de Janela de Cohen-Sutherland");
    }
}
