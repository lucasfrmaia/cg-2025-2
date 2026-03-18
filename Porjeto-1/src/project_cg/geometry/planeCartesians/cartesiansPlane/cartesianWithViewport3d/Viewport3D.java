package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport3d;

import org.lwjgl.opengl.GL11;
import project_cg.geometry.points.Point3D;

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
        drawViewportBounds();

        // Desenha o cubo na viewport
        cartesianPlane3D.drawCube(cartesianPlane3D.getCubeVertices());
    }

    private void drawViewportBounds() {
        float min = -10.0f;
        float max = 10.0f;

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glLineWidth(2.0f);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glBegin(GL11.GL_LINES);

        // Face frontal
        drawLine3D(min, min, min, max, min, min);
        drawLine3D(max, min, min, max, max, min);
        drawLine3D(max, max, min, min, max, min);
        drawLine3D(min, max, min, min, min, min);

        // Face traseira
        drawLine3D(min, min, max, max, min, max);
        drawLine3D(max, min, max, max, max, max);
        drawLine3D(max, max, max, min, max, max);
        drawLine3D(min, max, max, min, min, max);

        // Conexoes entre faces
        drawLine3D(min, min, min, min, min, max);
        drawLine3D(max, min, min, max, min, max);
        drawLine3D(max, max, min, max, max, max);
        drawLine3D(min, max, min, min, max, max);

        GL11.glEnd();
        GL11.glLineWidth(1.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void drawViewportFrameOverlay(int windowWidth, int windowHeight) {
        GL11.glViewport(0, 0, windowWidth, windowHeight);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, windowWidth, 0, windowHeight, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glLineWidth(2.5f);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(viewportX, viewportY);
        GL11.glVertex2f(viewportX + viewportWidth, viewportY);
        GL11.glVertex2f(viewportX + viewportWidth, viewportY + viewportHeight);
        GL11.glVertex2f(viewportX, viewportY + viewportHeight);
        GL11.glEnd();

        GL11.glLineWidth(1.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private void drawLine3D(float x1, float y1, float z1, float x2, float y2, float z2) {
        GL11.glVertex3f(x1, y1, z1);
        GL11.glVertex3f(x2, y2, z2);
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
