import view.mainScreen.MainScreenSingleton;
import view.MainScreenV2;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainScreenV2(MainScreenSingleton.getMainScreen());
        });
    }
}
