package view.utils;

import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataOptions {

    private final Map<String, Map<String, ShapePanel>> data;

    public DataOptions() {
        this.data = new LinkedHashMap<>();
    }

    public void addOption(String category, String option, ShapePanel panelInputs) {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();

        if (mainScreen.JPanelHandler.getPanelByCategory(category) == null) {
            throw new IllegalArgumentException("A Cateogria precisa de uma tela principal");
        }

        data.computeIfAbsent(category, k -> new LinkedHashMap<>()).put(option, panelInputs);
    }

    public String[] getFirstComboBoxOptions() {
        return data.keySet().toArray(new String[0]);
    }

    public String[] getSecondComboBoxOptions(String category) {
        return data.containsKey(category) ? data.get(category).keySet().toArray(new String[0]) : new String[0];
    }

    public JPanel getPanelInputsForOption(String option) {

        for (Map<String, ShapePanel> options : data.values()) {
            if (options.containsKey(option)) {
                var selected = options.get(option);
                selected.resetAll();
                return selected;
            }
        }

        return null;
    }
}

