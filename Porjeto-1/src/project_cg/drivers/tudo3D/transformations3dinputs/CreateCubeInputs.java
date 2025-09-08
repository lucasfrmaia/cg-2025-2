package project_cg.drivers.tudo3D.transformations3dinputs;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.geometry3d.points3d.Point3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import view.utils.ShapePanel;

import javax.swing.*;
import java.util.Arrays;

public class CreateCubeInputs extends ShapePanel {

    private JTextField pointInput1;
    private JTextField pointInput2;
    private JTextField pointInput3;
    private JTextField pointInput4;
    private JTextField pointInput5;
    private JTextField pointInput6;
    private JTextField pointInput7;
    private JTextField pointInput8;

    @Override
    protected void initializeInputs() {
        // Inicializa os campos de entrada para os 8 vértices do cubo
        pointInput1 = new JTextField(50);
        pointInput2 = new JTextField(50);
        pointInput3 = new JTextField(50);
        pointInput4 = new JTextField(50);
        pointInput5 = new JTextField(50);
        pointInput6 = new JTextField(50);
        pointInput7 = new JTextField(50);
        pointInput8 = new JTextField(50);

        // Adiciona os campos de entrada com os rótulos apropriados
        addInputField("Digite o (x, y, z) do Ponto 1:", pointInput1);
        addInputField("Digite o (x, y, z) do Ponto 2:", pointInput2);
        addInputField("Digite o (x, y, z) do Ponto 3:", pointInput3);
        addInputField("Digite o (x, y, z) do Ponto 4:", pointInput4);
        addInputField("Digite o (x, y, z) do Ponto 5:", pointInput5);
        addInputField("Digite o (x, y, z) do Ponto 6:", pointInput6);
        addInputField("Digite o (x, y, z) do Ponto 7:", pointInput7);
        addInputField("Digite o (x, y, z) do Ponto 8:", pointInput8);
    }

    @Override
    protected void onCalculate() {
        try {
            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

            // Processa os textos de entrada para criar os pontos 3D
            Point3D p1 = createPointFromInput(pointInput1);
            Point3D p2 = createPointFromInput(pointInput2);
            Point3D p3 = createPointFromInput(pointInput3);
            Point3D p4 = createPointFromInput(pointInput4);
            Point3D p5 = createPointFromInput(pointInput5);
            Point3D p6 = createPointFromInput(pointInput6);
            Point3D p7 = createPointFromInput(pointInput7);
            Point3D p8 = createPointFromInput(pointInput8);

            // Cria o array de vértices do cubo
            Point3D[] cubeVertices = {p1, p2, p3, p4, p5, p6, p7, p8};

            // Inicia o plano cartesiano 3D e desenha o cubo
            new Thread(() -> plane3D.update(cubeVertices)).start();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Coordenadas inválidas. Use números separados por espaços.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Point3D createPointFromInput(JTextField input) {
        // Processa o texto do campo de entrada para criar um ponto 3D
        double[] coords = Arrays.stream(input.getText().split(" "))
                .map(Double::parseDouble)
                .mapToDouble(Double::doubleValue)
                .toArray();

        if (coords.length != 3) {
            throw new IllegalArgumentException("Cada ponto deve ter exatamente 3 coordenadas (x, y, z).");
        }

        return new Point3D(coords[0], coords[1], coords[2]);
    }
}
