package view.utils;

import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public abstract class ShapePanel extends JPanel {
    protected JButton calculateButton;
    private int currentGridY;

    public ShapePanel() {
        setLayout(new GridBagLayout());
        setBackground(Constants.BACKGROUND_COLOR);
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
        calculateButton.setFont(Constants.UI_FONT);
        calculateButton.setBackground(Constants.SUCCESS_COLOR);
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setBorder(BorderFactory.createLineBorder(Constants.SUCCESS_COLOR, 2));
        calculateButton.setFocusPainted(false);
        calculateButton.setContentAreaFilled(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = currentGridY++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0); // More space above button
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
        setBackground(Constants.BACKGROUND_COLOR);
        calculateButton = new JButton("Calcular");
        currentGridY = 0;

        initializeInputs();
        addCalculateButton();
        calculateButton.addActionListener(e -> onCalculate());

        revalidate();
        repaint();
    }
}
