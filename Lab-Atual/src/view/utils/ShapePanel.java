package view.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public abstract class ShapePanel extends JPanel {
    protected JButton calculateButton;
    private int currentGridY;

    public ShapePanel() {
        setLayout(new GridBagLayout());
        calculateButton = new JButton("Calcular");
        currentGridY = 0;

        initializeInputs();
        addCalculateButton();
        calculateButton.addActionListener(e -> onCalculate());
    }

    @Override
    public int getHeight() {
        return 800;
    }

    @Override
    public int getWidth() {
        return 600;
    }

    protected abstract void initializeInputs();


    private void addCalculateButton() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = currentGridY++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(calculateButton, gbc);
    }


    protected void addInputField(String labelText, JTextField textField) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;


        gbc.gridx = 0;
        gbc.gridy = currentGridY;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel(labelText), gbc);


        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(textField, gbc);

        currentGridY++;
    }

    protected void addComboBox(String labelText, JComboBox<String> comboBox) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.gridy = currentGridY;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel(labelText), gbc);


        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(comboBox, gbc);

        currentGridY++;
    }


    protected abstract void onCalculate();

    public void resetAll() {
        removeAll();

        setLayout(new GridBagLayout());
        calculateButton = new JButton("Calcular");
        currentGridY = 0;

        initializeInputs();
        addCalculateButton();
        calculateButton.addActionListener(e -> onCalculate());

        revalidate();
        repaint();
    }
}
