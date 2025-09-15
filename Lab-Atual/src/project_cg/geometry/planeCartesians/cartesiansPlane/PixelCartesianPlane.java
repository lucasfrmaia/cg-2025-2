package project_cg.geometry.planeCartesians.cartesiansPlane;

import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane;
import project_cg.geometry.points.Point2D;

import java.awt.*;

// ToDo adaptar essa classe depois
public class PixelCartesianPlane extends BaseCartesianPlane {
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 800;
    private int DCX;
    private int DCY;
    private double NDCX;
    private double NDCY;
    private double DCXDisplay;
    private double DCYDisplay;

    public PixelCartesianPlane() {
        super();
    }

    public PixelCartesianPlane(int DCX, int DCY, double NDCX, double NDCY, double DCXDisplay, double DCYDisplay) {
        this.DCX = DCX;
        this.DCY = DCY;
        this.NDCX = NDCX;
        this.NDCY = NDCY;
        this.DCXDisplay = DCXDisplay;
        this.DCYDisplay = DCYDisplay;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawPlanoCartesiano(g);
        drawPoint(g, DCX, DCY);
    }

    private void drawPlanoCartesiano(Graphics g) {
        // Desenha o eixo X e Y
        int centerX = SCREEN_WIDTH / 2;
        int centerY = SCREEN_HEIGHT / 2;

        g.setColor(Color.BLACK);
        g.drawLine(0, centerY, SCREEN_WIDTH, centerY); // Eixo X
        g.drawLine(centerX, 0, centerX, SCREEN_HEIGHT); // Eixo Y

        // Desenha as linhas do grid com base em 100 pixels de intervalo
        g.setColor(Color.LIGHT_GRAY);

        // Linhas horizontais
        for (int i = centerY; i >= 0; i -= 100) {
            g.drawLine(0, i, getWidth(), i);
        }
        for (int i = centerY; i <= getHeight(); i += 100) {
            g.drawLine(0, i, getWidth(), i);
        }

        // Linhas verticais
        for (int i = centerX; i >= 0; i -= 100) {
            g.drawLine(i, 0, i, getHeight());
        }
        for (int i = centerX; i <= getWidth(); i += 100) {
            g.drawLine(i, 0, i, getHeight());
        }

        // Marcadores nos eixos
        g.setColor(Color.RED);
        for (int i = centerX; i <= getWidth(); i += 100) {
            g.drawLine(i, centerY - 5, i, centerY + 5); // Marcadores no eixo X (positivo)
        }
        for (int i = centerX; i >= 0; i -= 100) {
            g.drawLine(i, centerY - 5, i, centerY + 5); // Marcadores no eixo X (negativo)
        }
        for (int i = centerY; i <= getHeight(); i += 100) {
            g.drawLine(centerX - 5, i, centerX + 5, i); // Marcadores no eixo Y (positivo)
        }
        for (int i = centerY; i >= 0; i -= 100) {
            g.drawLine(centerX - 5, i, centerX + 5, i); // Marcadores no eixo Y (negativo)
        }
    }

    private void drawPoint(Graphics g, int x, int y) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, 1, 1); // Desenha um pequeno quadrado de 7x7 pixels para representar o ponto
    }

    // ToDo Atualizar depois
    @Override
    public void setPixel(Point2D coordinates, int rgb) {

    }

    @Override
    public int getPixel(int x, int y) {
        return 0;
    }

    @Override
    public void drawCartesianPlane() {

    }

    @Override
    public void clear() {

    }

    @Override
    public BaseCartesianPlane reset() {
        return new PixelCartesianPlane();
    }

}
