package project_cg.drivers.clipping;


import project_cg.geometry.points.Point2D;

public class CohenSutherlandLineClipper {

    // Códigos regionais
    private static final int INSIDE = 0; // 0000
    private static final int LEFT = 1;   // 0001
    private static final int RIGHT = 2;  // 0010
    private static final int BOTTOM = 4; // 0100
    private static final int TOP = 8;    // 1000

    private int xMin, yMin, xMax, yMax;

    public CohenSutherlandLineClipper(int xMin, int yMin, int xMax, int yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    private int computeCode(int x, int y) {
        int code = INSIDE;

        if (x < xMin) {
            code |= LEFT;
        } else if (x > xMax) {
            code |= RIGHT;
        }

        if (y < yMin) {
            code |= BOTTOM;
        } else if (y > yMax) {
            code |= TOP;
        }

        return code;
    }

    public Point2D[] clipLine(int x1, int y1, int x2, int y2) {
        int code1 = computeCode(x1, y1);
        int code2 = computeCode(x2, y2);
        boolean accept = false;

        while (true) {
            if ((code1 | code2) == 0) {
                // Ambos os pontos estão dentro da janela
                accept = true;
                break;
            } else if ((code1 & code2) != 0) {
                // Ambos os pontos estão fora da mesma região
                break;
            } else {
                // Pelo menos um dos pontos está fora
                int codeOut;
                double x = 0, y = 0;

                // Escolhe um ponto fora da janela
                if (code1 != 0) {
                    codeOut = code1;
                } else {
                    codeOut = code2;
                }

                // Calcula a interseção com as bordas
                if ((codeOut & TOP) != 0) {
                    x = x1 + (x2 - x1) * (yMax - y1) / (y2 - y1);
                    y = yMax;
                } else if ((codeOut & BOTTOM) != 0) {
                    x = x1 + (x2 - x1) * (yMin - y1) / (y2 - y1);
                    y = yMin;
                } else if ((codeOut & RIGHT) != 0) {
                    y = y1 + (y2 - y1) * (xMax - x1) / (x2 - x1);
                    x = xMax;
                } else if ((codeOut & LEFT) != 0) {
                    y = y1 + (y2 - y1) * (xMin - x1) / (x2 - x1);
                    x = xMin;
                }

                // Atualiza o ponto fora da janela
                if (codeOut == code1) {
                    x1 = (int) x;
                    y1 = (int) y;
                    code1 = computeCode(x1, y1);
                } else {
                    x2 = (int) x;
                    y2 = (int) y;
                    code2 = computeCode(x2, y2);
                }
            }
        }

        if (accept) {
            return new Point2D[]{new Point2D(x1, y1), new Point2D(x2, y2)};
        } else {
            return null; // Linha completamente fora da janela
        }
    }
}
