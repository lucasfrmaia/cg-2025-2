package project_cg.inputsPanel.recorteInputs;

import view.utils.ShapePanel;

import javax.swing.*;

public class SizeWindowInput extends ShapePanel {

    private JTextField heightScreen;

    private JTextField widthScreen;

    @Override
    protected void initializeInputs() {
        heightScreen = new JTextField(15);
        widthScreen = new JTextField(15);

        addInputField("Digite a altura da tela", widthScreen);
        addInputField("Digite a largura da tela", widthScreen);
    }

    @Override
    protected void onCalculate() {

    }

}
