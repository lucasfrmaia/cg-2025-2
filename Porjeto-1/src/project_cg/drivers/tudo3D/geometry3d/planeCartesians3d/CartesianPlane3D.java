package project_cg.drivers.tudo3D.geometry3d.planeCartesians3d;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import project_cg.drivers.viewport3d.Viewport3D;
import view.utils.BaseJPanel;

public class CartesianPlane3D extends BaseJPanel {
    private long window;
    private Point3D[] cubeVertices;
    private Viewport3D viewport3D;

    public CartesianPlane3D() {
        cubeVertices = new Point3D[]{
            new Point3D(0, 0, 0), new Point3D(1, 0, 0),
            new Point3D(1, 1, 0), new Point3D(0, 1, 0),
            new Point3D(0, 0, 1), new Point3D(1, 0, 1),
            new Point3D(1, 1, 1), new Point3D(0, 1, 1)
        };
        
        viewport3D = new Viewport3D(800, 50, 400, 400, this); // Posição e tamanho da viewport
    }

    @Override
    public CartesianPlane3D reset() {
        this.resetCube();
        return this;
    }

    public void start() {
        init(); // Inicializa GLFW no thread principal
        loop();
    }

    private void init() {

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Falha ao inicializar GLFW");
        }

        window = GLFW.glfwCreateWindow(1300, 600, "Plano Cartesiano 3D com Cubo", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Falha ao criar a janela GLFW");
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GL.createCapabilities();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(-10, 10, -10, 10, -10, 10);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // Renderiza o plano cartesiano principal
            GL11.glViewport(0, 0, 800, 600); // Configura o viewport principal
            GL11.glLoadIdentity();
            GL11.glRotatef(45, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(45, 0.0f, 1.0f, 0.0f);
            drawAxes();
            drawCube(cubeVertices);
            
            // Renderiza o conteúdo da viewport 3D
            viewport3D.renderViewport();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public void update(Point3D[] newVertices) {
        cubeVertices = newVertices;
    }

    public Point3D[] getCubeVertices() {
        return cubeVertices;
    }

    public void setCubeVertices(Point3D[] cubeVertices) {
        this.cubeVertices = cubeVertices;
    }

    public void drawAxes() {
        GL11.glBegin(GL11.GL_LINES);

        GL11.glColor3f(1.0f, 0.0f, 0.0f);
        GL11.glVertex3f(-10.0f, 0.0f, 0.0f);
        GL11.glVertex3f(10.0f, 0.0f, 0.0f);

        GL11.glColor3f(0.0f, 1.0f, 0.0f);
        GL11.glVertex3f(0.0f, -10.0f, 0.0f);
        GL11.glVertex3f(0.0f, 10.0f, 0.0f);

        GL11.glColor3f(0.0f, 0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 0.0f, -10.0f);
        GL11.glVertex3f(0.0f, 0.0f, 10.0f);

        GL11.glEnd();
    }

    public void drawCube(Point3D[] vertices) {
        if (vertices.length != 8) {
            throw new IllegalArgumentException("O array de vértices deve conter exatamente 8 pontos para formar um cubo.");
        }

        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glBegin(GL11.GL_LINES);

        desenhaLinhaNo3d(vertices[0], vertices[1]);
        desenhaLinhaNo3d(vertices[1], vertices[2]);
        desenhaLinhaNo3d(vertices[2], vertices[3]);
        desenhaLinhaNo3d(vertices[3], vertices[0]);

        desenhaLinhaNo3d(vertices[4], vertices[5]);
        desenhaLinhaNo3d(vertices[5], vertices[6]);
        desenhaLinhaNo3d(vertices[6], vertices[7]);
        desenhaLinhaNo3d(vertices[7], vertices[4]);

        desenhaLinhaNo3d(vertices[0], vertices[4]);
        desenhaLinhaNo3d(vertices[1], vertices[5]);
        desenhaLinhaNo3d(vertices[2], vertices[6]);
        desenhaLinhaNo3d(vertices[3], vertices[7]);

        GL11.glEnd();
    }

    private void desenhaLinhaNo3d(Point3D start, Point3D end) {
        GL11.glVertex3f((float) start.getX(), (float) start.getY(), (float) start.getZ());
        GL11.glVertex3f((float) end.getX(), (float) end.getY(), (float) end.getZ());
    }

    public void resetCube() {
        cubeVertices = new Point3D[]{
            new Point3D(0, 0, 0), new Point3D(1, 0, 0),
            new Point3D(1, 1, 0), new Point3D(0, 1, 0),
            new Point3D(0, 0, 1), new Point3D(1, 0, 1),
            new Point3D(1, 1, 1), new Point3D(0, 1, 1)
        };
        update(cubeVertices);
    }
}