package project_cg.inputsPanel.recorteInputs;

import project_cg.geometry.planeCartesians.cartesiansPlane.LineClippingPlane;
import utils.BaseJPanel;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class SizeWindowInput extends ShapePanel {

    private static final int DEFAULT_VIEWPORT_WIDTH = 300;
    private static final int DEFAULT_VIEWPORT_HEIGHT = 220;

    private JTextField heightScreen;

    private JTextField widthScreen;

    @Override
    protected String getLabelButtonCalcular() {
        return "Definir";
    }

    @Override
    protected void initializeInputs() {
        heightScreen = new JTextField(15);
        widthScreen = new JTextField(15);

        widthScreen.setText(String.valueOf(DEFAULT_VIEWPORT_WIDTH));
        heightScreen.setText(String.valueOf(DEFAULT_VIEWPORT_HEIGHT));

        addInputField("Largura da viewport (default 300):", widthScreen);
        addInputField("Altura da viewport (default 220):", heightScreen);
    }

    @Override
    protected void onCalculate() {
        try {
            int width = Integer.parseInt(widthScreen.getText().trim());
            int height = Integer.parseInt(heightScreen.getText().trim());

            LineClippingPlane plane = getRecortePlane();
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

