package view.mainScreen;

import javax.swing.*;

public class MainScreenSingleton {
    private static MainScreen mainScreen;

    public static MainScreen getMainScreen() {

        if (mainScreen == null) {
            mainScreen = new MainScreen(new JPanel());
        }

        return mainScreen;
    }


    public static JComboBox<String> getComboBoxGeometriFigures() {
        var mainScreen = getMainScreen();
        return mainScreen.geometricFiguresHandler.getComboBoxFigures();
    }

}
