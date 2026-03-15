package view.mainScreen;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.util.ArrayList;
import java.util.List;

public class MainScreenSingleton {
    private static MainScreen mainScreen;
    private static final List<JComboBox<String>> geometricFigureCombos = new ArrayList<>();

    public static MainScreen getMainScreen() {

        if (mainScreen == null) {
            mainScreen = new MainScreen(new JPanel());
        }

        return mainScreen;
    }


    public static JComboBox<String> getComboBoxGeometriFigures() {
        JComboBox<String> comboBox = new JComboBox<>();

        refreshComboBoxGeometriFigures(comboBox);
        geometricFigureCombos.add(comboBox);

        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                Object selected = comboBox.getSelectedItem();
                refreshComboBoxGeometriFigures(comboBox);

                if (selected != null) {
                    comboBox.setSelectedItem(selected);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        return comboBox;
    }

    public static void refreshAllComboBoxGeometriFigures() {
        for (JComboBox<String> comboBox : geometricFigureCombos) {
            if (comboBox == null) {
                continue;
            }

            Object selected = comboBox.getSelectedItem();
            refreshComboBoxGeometriFigures(comboBox);

            if (selected != null) {
                comboBox.setSelectedItem(selected);
            }
        }
    }

    private static void refreshComboBoxGeometriFigures(JComboBox<String> comboBox) {
        var mainScreen = getMainScreen();

        comboBox.removeAllItems();

        if (mainScreen.geometricFiguresHandler == null) {
            return;
        }

        JComboBox<String> currentFigures = mainScreen.geometricFiguresHandler.getComboBoxFigures();

        for (int i = 0; i < currentFigures.getItemCount(); i++) {
            comboBox.addItem(currentFigures.getItemAt(i));
        }
    }

}
