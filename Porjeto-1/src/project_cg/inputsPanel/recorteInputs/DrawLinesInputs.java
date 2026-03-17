package project_cg.inputsPanel.recorteInputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.LineClippingPlane;
import utils.BaseJPanel;
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
    protected String getLabelButtonCalcular() {
        return "Calcular";
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

            LineClippingPlane plane = getRecortePlane();
            plane.generateRandomLines(quantity);

            JOptionPane.showMessageDialog(
                    this,
                    "Foram adicionadas " + quantity + " linhas aleatorias. Total atual: " + plane.getOriginalLineCount() + "."
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite uma quantidade inteira valida.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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

