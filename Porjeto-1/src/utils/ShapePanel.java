package utils;

import javax.swing.*;
import java.awt.*;


public abstract class ShapePanel extends JPanel {
    protected JButton calculateButton;
    private int currentGridY;

    protected boolean isLeftAligned() {
        return false;
    }

    public ShapePanel() {
        setLayout(new GridBagLayout());
        calculateButton = new JButton(getLabelButtonCalcular());
        currentGridY = 0;

        initializeInputs();
        addCalculateButton();
        calculateButton.addActionListener(e -> onCalculate());
    }

    protected abstract void initializeInputs();

    protected abstract String getLabelButtonCalcular();


    private void addCalculateButton() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = currentGridY++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(calculateButton, gbc);
    }


    protected void addInputField(String labelText, JTextField textField) {
        normalizeTextFieldSize(textField);

        GridBagConstraints gbc = new GridBagConstraints();
        int leftPadding = isLeftAligned() ? 2 : 5;
        gbc.insets = new Insets(4, leftPadding, 2, 4);
        gbc.gridx = 0;
        gbc.gridy = currentGridY;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel(labelText), gbc);

        gbc.gridy = currentGridY + 1;
        gbc.insets = new Insets(0, leftPadding, 6, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(textField, gbc);

        currentGridY += 2;
    }

    private void normalizeTextFieldSize(JTextField textField) {
        Dimension compactSize = new Dimension(
                Constants.INPUT_TEXT_FIELD_WIDTH,
                Constants.INPUT_TEXT_FIELD_HEIGHT
        );

        textField.setPreferredSize(compactSize);
        textField.setMinimumSize(compactSize);
    }

    protected void addComboBox(String labelText, JComboBox<String> comboBox) {
        normalizeComboBoxSize(comboBox);

        GridBagConstraints gbc = new GridBagConstraints();
        int leftPadding = isLeftAligned() ? 2 : 5;
        gbc.gridx = 0;
        gbc.gridy = currentGridY;
        gbc.insets = new Insets(4, leftPadding, 2, 4);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel(labelText), gbc);

        gbc.gridy = currentGridY + 1;
        gbc.insets = new Insets(0, leftPadding, 6, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(comboBox, gbc);

        currentGridY += 2;
    }

    private void normalizeComboBoxSize(JComboBox<String> comboBox) {
        Dimension compactSize = new Dimension(
                Constants.INPUT_COMBO_BOX_WIDTH,
                Constants.INPUT_COMBO_BOX_HEIGHT
        );

        comboBox.setPreferredSize(compactSize);
        comboBox.setMinimumSize(compactSize);
        comboBox.setMaximumSize(compactSize);
    }


    protected abstract void onCalculate();

    public void resetAll() {
        removeAll();

        setLayout(new GridBagLayout());
        calculateButton = new JButton(getLabelButtonCalcular());
        currentGridY = 0;

        initializeInputs();
        addCalculateButton();
        calculateButton.addActionListener(e -> onCalculate());

        revalidate();
        repaint();
    }
}
