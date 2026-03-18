package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport3d;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import project_cg.geometry.points.Point3D;
import project_cg.transformations3d.Reflection3D;
import project_cg.transformations3d.Rotation3D;
import project_cg.transformations3d.Scale3D;
import project_cg.transformations3d.Shear3D;
import project_cg.transformations3d.Translation3D;
import utils.BaseJPanel;
import utils.Matrix;

import java.util.ArrayList;
import java.util.List;

public class CartesianPlane3D extends BaseJPanel {
    private long window;
    private Point3D[] cubeVertices;
    private Viewport3D viewport3D;
    private final List<Object> pendingTransformations;

    public CartesianPlane3D() {
        cubeVertices = new Point3D[]{
            new Point3D(0, 0, 0), new Point3D(1, 0, 0),
            new Point3D(1, 1, 0), new Point3D(0, 1, 0),
            new Point3D(0, 0, 1), new Point3D(1, 0, 1),
            new Point3D(1, 1, 1), new Point3D(0, 1, 1)
        };

        pendingTransformations = new ArrayList<>();
        
        viewport3D = new Viewport3D(800, 50, 400, 400, this); // Posição e tamanho da viewport
    }

    @Override
    public CartesianPlane3D reset() {
        this.resetCube();
        clearQueuedTransformations();
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

        // Mantem a janela 3D sempre acima das demais, equivalente ao alwaysOnTop.
        GLFW.glfwWindowHint(GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(1300, 600, "Plano Cartesiano 3D com Cubo", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Falha ao criar a janela GLFW");
        }

        GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);

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
            viewport3D.drawViewportFrameOverlay(1300, 600);

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

    public void queueTransformation(Object transformation) {
        if (!(transformation instanceof Translation3D
                || transformation instanceof Rotation3D
                || transformation instanceof Scale3D
                || transformation instanceof Shear3D
                || transformation instanceof Reflection3D)) {
            throw new IllegalArgumentException("Tipo de transformacao 3D invalido.");
        }

        pendingTransformations.add(transformation);
    }

    public int getPendingTransformationsCount() {
        return pendingTransformations.size();
    }

    public String getPendingTransformationsSummary() {
        if (pendingTransformations.isEmpty()) {
            return "Nenhuma";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pendingTransformations.size(); i++) {
            if (i > 0) {
                builder.append(" -> ");
            }
            builder.append(pendingTransformations.get(i).toString());
        }

        return builder.toString();
    }

    public void clearQueuedTransformations() {
        pendingTransformations.clear();
    }

    public void applyQueuedTransformations() {
        if (cubeVertices == null || cubeVertices.length != 8) {
            throw new IllegalStateException("Vertices invalidos ou ausentes.");
        }

        if (pendingTransformations.isEmpty()) {
            throw new IllegalStateException("Nao ha transformacoes acumuladas para aplicar.");
        }

        for (Object transformation : pendingTransformations) {
            applyTransformationInQueueOrder(transformation);
        }

        update(cubeVertices);
        clearQueuedTransformations();
    }

    private void applyTransformationInQueueOrder(Object transformation) {
        double[][] transformationMatrix = getTransformationMatrix(transformation);

        if (!(transformation instanceof Translation3D) && !(transformation instanceof Reflection3D)) {
            Point3D focalPoint = getFirstPointAsFocalPoint(cubeVertices);
            transformationMatrix = composeMatrixAroundFocalPoint(transformationMatrix, focalPoint);
        }

        applyMatrixToCube(transformationMatrix);
    }

    private double[][] composeMatrixAroundFocalPoint(double[][] transformationMatrix, Point3D focalPoint) {
        double[][] toOrigin = new Translation3D(-focalPoint.getX(), -focalPoint.getY(), -focalPoint.getZ()).getTransformation();
        double[][] backToFocal = new Translation3D(focalPoint.getX(), focalPoint.getY(), focalPoint.getZ()).getTransformation();
        return Matrix.multiply(Matrix.multiply(toOrigin, transformationMatrix), backToFocal);
    }

    private double[][] getTransformationMatrix(Object transformation) {
        if (transformation instanceof Translation3D) {
            return ((Translation3D) transformation).getTransformation();
        }

        if (transformation instanceof Rotation3D) {
            return ((Rotation3D) transformation).getTransformation();
        }

        if (transformation instanceof Scale3D) {
            return ((Scale3D) transformation).getTransformation();
        }

        if (transformation instanceof Shear3D) {
            return ((Shear3D) transformation).getTransformation();
        }

        if (transformation instanceof Reflection3D) {
            return ((Reflection3D) transformation).getTransformation();
        }

        throw new IllegalArgumentException("Tipo de transformacao 3D nao suportado: " + transformation);
    }

    private void applyMatrixToCube(double[][] matrix) {
        for (int i = 0; i < cubeVertices.length; i++) {
            cubeVertices[i] = multiplyPointByMatrix(cubeVertices[i], matrix);
        }
    }

    private Point3D multiplyPointByMatrix(Point3D point, double[][] matrix) {
        double[][] pointHomogeneous = new double[][] {
                { point.getX(), point.getY(), point.getZ(), 1 }
        };

        double[][] result = Matrix.multiply(pointHomogeneous, matrix);

        return new Point3D(result[0][0], result[0][1], result[0][2]);
    }

    private Point3D getFirstPointAsFocalPoint(Point3D[] vertices) {
        if (vertices.length == 0) {
            throw new IllegalStateException("Nao foi possivel obter o ponto focal do cubo.");
        }

        return new Point3D(
                vertices[0].getX(),
                vertices[0].getY(),
                vertices[0].getZ()
        );
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