package project_cg.drivers;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.transformations3dinputs.*;

import javax.swing.*;
import java.awt.*;

public class Main3DViewer {

    /*
    public static void main(String[] args) {
        CartesianPlane3D plane3D = new CartesianPlane3D();

        // Thread para executar o OpenGL principal
        Thread renderThread = new Thread(plane3D::start);
        renderThread.start();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Transformações 3D - Plano Cartesiano");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(420, 600);
            frame.setLayout(new BorderLayout());

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Rotação", new Rotation3DInputs(plane3D));
            tabbedPane.addTab("Reflexão", new Reflection3DInputs(plane3D));
            tabbedPane.addTab("Escala", new Scale3DInputs(plane3D));
            tabbedPane.addTab("Cisalhamento", new Shear3DInputs(plane3D));
            tabbedPane.addTab("Translação", new Translation3DInputs(plane3D));

            JPanel controlPanel = new JPanel(new BorderLayout());
            controlPanel.add(tabbedPane, BorderLayout.CENTER);

            // Botão "Limpar"
            JButton clearButton = new JButton("Limpar");
            clearButton.addActionListener(e -> {
                plane3D.resetCube();
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(clearButton);
            controlPanel.add(buttonPanel, BorderLayout.SOUTH);

            controlPanel.setPreferredSize(new Dimension(400, 800));
            frame.add(controlPanel, BorderLayout.EAST);
            frame.setVisible(true);
        });

    }
     */
}


