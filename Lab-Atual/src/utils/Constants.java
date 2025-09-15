package utils;

import java.awt.*;

public class Constants {
    public static final int WIDTH_MAIN_SCREEN = 1300;
    public static final int HEIGHT_MAIN_SCREEN = 860;
    public static final int WIDTH_CARTESIAN_PLANE = (int) (WIDTH_MAIN_SCREEN * 0.99);
    public static final int HEIGHT_CARTESIAN_PLANE = (int) (HEIGHT_MAIN_SCREEN * 0.85);
    public static final String DEFAULT_OPTION_CHECKBOX = "Selecione uma opção...";
    public static final String DISABLED_OPTION_SELECT = "";
    public static final int COLOR_LINES_CARTESIAN_PLANE = Color.BLACK.getRGB();
    public static final int BACKGROUND_CARTESIAN_PLANE = Color.WHITE.getRGB();
    public static final int COLOR_PRIMITEVES = new Color(0, 123, 255).getRGB(); // Primary blue

    public static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    public static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    public static final Color TEXT_COLOR = Color.BLACK;

    public static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 12);
}
