package project_cg.inputsPanel.recorteInputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.RecorteSutherlandPlane;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class SizeWindowInput extends ShapePanel {

    private JTextField heightScreen;

    private JTextField widthScreen;

    @Override
    protected void initializeInputs() {
        heightScreen = new JTextField(15);
        widthScreen = new JTextField(15);

        addInputField("Largura da viewport:", widthScreen);
        addInputField("Altura da viewport:", heightScreen);
    }

    @Override
    protected void onCalculate() {
        try {
            int width = Integer.parseInt(widthScreen.getText().trim());
            int height = Integer.parseInt(heightScreen.getText().trim());

            RecorteSutherlandPlane plane = getRecortePlane();
            plane.setViewportSize(width, height);

            JOptionPane.showMessageDialog(
                    this,
                    "Viewport atualizada para " + width + "x" + height + "."
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite largura e altura inteiras validas.");
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
