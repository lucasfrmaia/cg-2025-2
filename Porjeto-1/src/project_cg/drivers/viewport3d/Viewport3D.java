package project_cg.drivers.viewport3d;

import org.lwjgl.opengl.GL11;
import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;

public class Viewport3D {
    private final int viewportWidth;
    private final int viewportHeight;
    private final int viewportX;
    private final int viewportY;
    private final CartesianPlane3D cartesianPlane3D;

    public Viewport3D(int x, int y, int width, int height, CartesianPlane3D cartesianPlane3D) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("A largura e altura da viewport devem ser maiores que zero.");
        }
        this.viewportX = x;
        this.viewportY = y;
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.cartesianPlane3D = cartesianPlane3D;
    }

    public void renderViewport() {
        // Configura o recorte para a viewport
        GL11.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

        // Configura a matriz de projeção para a viewport
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(-10, 10, -10, 10, -10, 10);

        // Configura a matriz de modelo/visão
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        applyInitialTransformations();
        drawViewportContent();
    }

    private void applyInitialTransformations() {
        // Aplica transformações iniciais (pode ser configurável no futuro)
        GL11.glRotatef(45, 1.0f, 0.0f, 0.0f); // Rotação no eixo X
        GL11.glRotatef(45, 0.0f, 1.0f, 0.0f); // Rotação no eixo Y
    }

    private void drawViewportContent() {
        // Desenha o cubo na viewport
        cartesianPlane3D.drawCube(cartesianPlane3D.getCubeVertices());
    }

    public void update(Point3D[] updatedVertices) {
        if (updatedVertices == null || updatedVertices.length == 0) {
            throw new IllegalArgumentException("Os vértices atualizados não podem ser nulos ou vazios.");
        }
        cartesianPlane3D.setCubeVertices(updatedVertices);
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }
}
