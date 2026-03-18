package project_cg.geometry.clipping;

import project_cg.geometry.points.Point2D;

public class CohenSutherlandLineClipper {

    /* Define um código de quatro bits para cada uma das regiões externas de uma 
     * janela de recorte retangular. (Mesma nomenclatura do C++) */
    private static final int winLeftBitCode = 0x1;   // 0001
    private static final int winRightBitCode = 0x2;  // 0010
    private static final int winBottomBitCode = 0x4; // 0100
    private static final int winTopBitCode = 0x8;    // 1000

    private double winMinX, winMinY, winMaxX, winMaxY;

    public CohenSutherlandLineClipper(int xMin, int yMin, int xMax, int yMax) {
        this.winMinX = xMin;
        this.winMinY = yMin;
        this.winMaxX = xMax;
        this.winMaxY = yMax;
    }

    private boolean inside(int code) {
        return code == 0; 
    }

    private boolean reject(int code1, int code2) {
        return (code1 & code2) != 0;
    }

    private boolean accept(int code1, int code2) {
        return (code1 | code2) == 0;
    }

    private int encode(double ptX, double ptY) {
        int code = 0x00;
        if (ptX < winMinX)
            code = code | winLeftBitCode;
        if (ptX > winMaxX)
            code = code | winRightBitCode;
        if (ptY < winMinY)
            code = code | winBottomBitCode;
        if (ptY > winMaxY)
            code = code | winTopBitCode;
        
        return code;
    }

    public Point2D[] clipLine(int x1, int y1, int x2, int y2) {
        int code1, code2;
        boolean done = false, plotLine = false;
        double m = 0.0;

        double p1x = x1;
        double p1y = y1;
        double p2x = x2;
        double p2y = y2;

        while (!done) {
            code1 = encode(p1x, p1y);
            code2 = encode(p2x, p2y);

            if (accept(code1, code2)) {
                done = true;
                plotLine = true;
            } else if (reject(code1, code2)) {
                done = true;
            } else {
                /* Rotula o ponto final fora da janela de exibição como p1 (Swap) */
                if (inside(code1)) {
                    // swapPts
                    double tmpX = p1x; p1x = p2x; p2x = tmpX;
                    double tmpY = p1y; p1y = p2y; p2y = tmpY;
                    // swapCodes
                    int tmpCode = code1; code1 = code2; code2 = tmpCode;
                }

                /* Usa a inclinação 'm' para encontrar a interseção */
                if (p2x != p1x) {
                    m = (p2y - p1y) / (p2x - p1x);
                }

                if ((code1 & winLeftBitCode) != 0) {
                    p1y += (winMinX - p1x) * m;
                    p1x = winMinX;
                } 
                else if ((code1 & winRightBitCode) != 0) {
                    p1y += (winMaxX - p1x) * m;
                    p1x = winMaxX;
                } 
                else if ((code1 & winBottomBitCode) != 0) {
                    /* Precisa atualizar p1x apenas para linhas não verticais. */
                    if (p2x != p1x) {
                        p1x += (winMinY - p1y) / m;
                    }
                    p1y = winMinY;
                } 
                else if ((code1 & winTopBitCode) != 0) {
                    if (p2x != p1x) {
                        p1x += (winMaxY - p1y) / m;
                    }
                    p1y = winMaxY;
                }
            }
        }

        if (plotLine) {
            // Arredonda os valores finais para o pixel mais próximo (equivalente à função 'round' do C++)
            int plotX1 = (int) Math.round(p1x);
            int plotY1 = (int) Math.round(p1y);
            int plotX2 = (int) Math.round(p2x);
            int plotY2 = (int) Math.round(p2y);
            
            return new Point2D[]{new Point2D(plotX1, plotY1), new Point2D(plotX2, plotY2)};
        }

        return null; // A linha foi rejeitada (completamente fora)
    }
}