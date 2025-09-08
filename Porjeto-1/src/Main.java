import view.mainScreen.MainScreen;
import view.mainScreen.mainScreenPanels.InputsPanel;
import view.mainScreen.MainScreenSingleton;
import view.utils.GeometricFiguresHandler;

public class Main {
    public static void main(String[] args) {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        mainScreen.setInputs(new InputsPanel());
        mainScreen.setLocationRelativeTo(null);
        mainScreen.setLayoutPanel();
        mainScreen.setVisible(true);
    }
}
