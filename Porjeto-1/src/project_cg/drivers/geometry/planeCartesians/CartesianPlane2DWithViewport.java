package project_cg.drivers.geometry.planeCartesians;

import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.Viewport2D;
import project_cg.geometry.planeCartesians.cartesiansPlane.CartesianPlane2D;
import project_cg.geometry.points.Point2D;

import java.awt.*;

public class CartesianPlane2DWithViewport extends CartesianPlane2D {

    private final Viewport2D viewport;

    public CartesianPlane2DWithViewport() {
        super();
        this.viewport = new Viewport2D(450, 50, 200, 200); // Posição e tamanho da viewport
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Atualizar e desenhar a viewport
        viewport.renderFromCartesian(this, -50, -50, 50, 50); // Define a janela de recorte
        viewport.draw(g);
    }

    @Override
    public void setPixel(Point2D point, int rgb) {
        super.setPixel(point, rgb);
    }

}
