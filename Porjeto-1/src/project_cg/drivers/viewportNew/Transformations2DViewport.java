package project_cg.drivers.viewportNew;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import project_cg.geometry.figures.Square;
import project_cg.geometry.planeCartesians.cartesiansPlane.PixelCartesianPlane;
import project_cg.geometry.points.Point2D;
import project_cg.primitives.MidpointLine;

import java.util.function.Consumer;

public class Transformations2DViewport {

    private long window;
    private int width = 800;
    private int height = 600;
    private int viewportX = 50;
    private int viewportY = 50;
    private int viewportWidth = 400;
    private int viewportHeight = 300;
    private PixelCartesianPlane cartesianPlane;
    private Square square; // Objeto Square para desenhar na viewport

    // Definindo a janela de corte (primeiro quadrante)
    private float xMin = 0;
    private float yMin = 0;
    private float xMax = 600; // Definindo a metade direita da tela como janela de corte
    private float yMax = 400; // Definindo a metade superior da tela como janela de corte

    public Transformations2DViewport(PixelCartesianPlane plane, Square square) {
        this.cartesianPlane = plane;
        this.square = square;
    }

    public void run() {
        init();
        loop();

        // Encerrar GLFW
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private void init() {
        // Inicializa o GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Não foi possível inicializar o GLFW");
        }

        // Cria a janela
        window = GLFW.glfwCreateWindow(width, height, "Transformação Window-to-Viewport", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Erro ao criar janela GLFW");
        }

        // Configura o contexto OpenGL
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();
    }

    private void applyViewport() {
        GL11.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
    }

    private void setupWindowToViewportTransformation() {
        // Calcula a escala para a transformação da janela para a viewport
        float scaleX = (float)(viewportWidth) / (xMax - xMin);
        float scaleY = (float)(viewportHeight) / (yMax - yMin);

        // Configura a matriz de transformação
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        // Aplica a escala e a translação para mapear a janela de corte para a viewport
        GL11.glTranslatef(-xMin, -yMin, 0); // Translada a origem da janela de corte para (0, 0)
        GL11.glScalef(scaleX, scaleY, 1); // Escala a janela de corte para caber na viewport
        GL11.glTranslatef(viewportX, viewportY, 0); // Translada para a posição inicial da viewport

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    private void drawSquareInViewport() {
        // Define a cor para as linhas do quadrado
        GL11.glColor3f(0.0f, 0.0f, 1.0f); // Azul

        // Desenha as linhas do quadrado usando os vértices transformados
        GL11.glBegin(GL11.GL_LINE_LOOP);

        // Obtém e transforma os vértices usando o método getVertex da classe Square
        Consumer<Point2D> vertexConsumer = vertex -> {
            // Aplica a transformação Window-to-Viewport para cada vértice
            int transformedX = (int) (viewportX + (vertex.getX() - xMin) * viewportWidth / (xMax - xMin));
            int transformedY = (int) (viewportY + (vertex.getY() - yMin) * viewportHeight / (yMax - yMin));
            GL11.glVertex2i(transformedX, transformedY); // Usando glVertex2i para valores inteiros
        };

        square.getVertex(vertexConsumer);

        GL11.glEnd();
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            // Limpa a tela
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // Aplica o viewport
            applyViewport();

            // Configura a transformação da janela de corte para a viewport
            setupWindowToViewportTransformation();

            // Desenha o quadrado transformado na viewport
            drawSquareInViewport();

            // Atualiza a tela
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        PixelCartesianPlane cartesianPlane = new PixelCartesianPlane();
        
        // Criando o quadrado com os vértices e o algoritmo de linha (exemplo com MidpointLine)
        Point2D[] vertices = {
            new Point2D(100, 100),
            new Point2D(200, 100),
            new Point2D(200, 200),
            new Point2D(100, 200)
        };

        Square square = new Square(vertices, new MidpointLine());

        Transformations2DViewport viewport = new Transformations2DViewport(cartesianPlane, square);
        viewport.run();
    }
}
