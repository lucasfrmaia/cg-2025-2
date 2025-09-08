package view.select;

import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.CartesianPlane2DWithViewport;
import utils.Constants;
import view.mainScreen.MainScreen;
import view.utils.DataOptions;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;


public class SelectOptions extends JPanel {
    private static class OptionPanel extends JPanel {
        public OptionPanel() {
            setLayout(new BorderLayout());
        }

        public void updatePanel(JPanel newPanel) {
            removeAll();

            if (newPanel != null) {
                add(newPanel, BorderLayout.CENTER);
            }

            revalidate();
            repaint();
        }
    }

    private JComboBox<String> comboBox1;
    private JComboBox<String> comboBox2;
    private DefaultComboBoxModel<String> modelComboBox2;
    private OptionPanel optionPanel;
    private final DataOptions dataOptions;

    public SelectOptions(DataOptions dataOptions) {
        this.dataOptions = dataOptions;
        drawPanel();
    }

    public void drawPanel() {
        setLayout(new BorderLayout());

        optionPanel = new OptionPanel();

        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.setLayout(new GridLayout(1, 2, 10, 10));
        add(comboBoxPanel, BorderLayout.SOUTH);

        comboBox1 = new JComboBox<>();

        comboBox1.addItem(Constants.DEFAULT_OPTION_CHECKBOX);

        for (String item : dataOptions.getFirstComboBoxOptions()) {
            comboBox1.addItem(item);
        }

        comboBoxPanel.add(comboBox1);

        modelComboBox2 = new DefaultComboBoxModel<>();
        comboBox2 = new JComboBox<>(modelComboBox2);
        comboBoxPanel.add(comboBox2);

        comboBox2.setEnabled(false);

        comboBox1.addActionListener(e -> {
            String selectedCategory = (String) comboBox1.getSelectedItem();

            if (Constants.DEFAULT_OPTION_CHECKBOX.equals(selectedCategory)) {
                comboBox2.setEnabled(false);
                modelComboBox2.removeAllElements();
                optionPanel.updatePanel(null);
            } else {
                comboBox2.setEnabled(true);
                MainScreen mainScreen = MainScreenSingleton.getMainScreen();
                updateComboBox2(dataOptions.getSecondComboBoxOptions(selectedCategory));

                CartesianPlane2DWithViewport cartesianPlane2DWithViewport = (CartesianPlane2DWithViewport) mainScreen.JPanelHandler.getPanelByCategory("Transformações");

                assert selectedCategory != null;
                if (selectedCategory.equals("Transformações")) {
                    cartesianPlane2DWithViewport.viewportWindow.enableViewport();
                } else {
                    cartesianPlane2DWithViewport.viewportWindow.disableViewport();
                }

                mainScreen.updateCurrentPanel(selectedCategory);


            }
        });

        comboBox2.addActionListener(e -> {
            String selectedOption = (String) comboBox2.getSelectedItem();

            if (selectedOption != null && !Constants.DEFAULT_OPTION_CHECKBOX.equals(selectedOption) && !selectedOption.equals(Constants.DISABLED_OPTION_SELECT)) {

                optionPanel.updatePanel(dataOptions.getPanelInputsForOption(selectedOption));

                JFrame inputsFrame = new JFrame();

                inputsFrame.setTitle("Projeto Computação Gráfica/Processamento de Imagem");
                inputsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                inputsFrame.setSize(800, 300);
                inputsFrame.setLocationRelativeTo(null);
                inputsFrame.setResizable(false);
                inputsFrame.add(optionPanel);

                optionPanel.revalidate();
                optionPanel.repaint();

                inputsFrame.setVisible(true);
            }

        });

        updateComboBox2(
                dataOptions.getSecondComboBoxOptions(
                        Objects.requireNonNull(comboBox1.getSelectedItem()).toString())
        );
    }

    private void updateComboBox2(String[] items) {
        modelComboBox2.removeAllElements();
        modelComboBox2.addElement(Constants.DEFAULT_OPTION_CHECKBOX);

        for (String item : items) {
            modelComboBox2.addElement(item);
        }

        comboBox2.setSelectedIndex(0);
    }
}
