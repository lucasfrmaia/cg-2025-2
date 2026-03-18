package utils;

import java.awt.*;

public class Constants {
    public static final int WIDTH_MAIN_SCREEN = 1400;
    public static final int HEIGHT_MAIN_SCREEN = 900;
    public static final int WIDTH_CARTESIAN_PLANE = (int) (WIDTH_MAIN_SCREEN * 0.58);
    public static final int HEIGHT_CARTESIAN_PLANE = (int) (HEIGHT_MAIN_SCREEN * 0.85);
    public static final int INPUT_SECTION_WIDTH = (int) (WIDTH_MAIN_SCREEN * 0.38);
    public static final int INPUT_SECTION_HEIGHT = (int) (HEIGHT_MAIN_SCREEN * 0.83);

    public static final int INPUT_SECTION_COLUMNS = 2;
    public static final int INPUT_SECTION_H_GAP = 10;
    public static final int INPUT_SECTION_V_GAP = 10;
    public static final int INPUT_TEXT_FIELD_WIDTH = 30;
    public static final int INPUT_TEXT_FIELD_HEIGHT = 24;
        public static final int INPUT_COMBO_BOX_WIDTH = Math.max(
            120,
            (INPUT_SECTION_WIDTH / INPUT_SECTION_COLUMNS) - 90
        );
        public static final int INPUT_COMBO_BOX_HEIGHT = 24;

    public static final int COLOR_LINES_CARTESIAN_PLANE = Color.WHITE.getRGB();
    public static final int BACKGROUND_CARTESIAN_PLANE = Color.BLACK.getRGB();
    public static final int COLOR_PRIMITEVES = Color.RED.getRGB();
}
