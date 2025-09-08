package project_cg.drivers.tudo3D.transformations3dinputs;

import javax.swing.*;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;

import java.awt.*;

public class CartesianPlane3DPanel extends JPanel {
    private final CartesianPlane3D cartesianPlane3D;

    public CartesianPlane3DPanel(CartesianPlane3D cartesianPlane3D) {
        this.cartesianPlane3D = cartesianPlane3D;
        setPreferredSize(new Dimension(800, 600)); // Define o tamanho padrão do painel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Renderize os eixos e o cubo diretamente no JPanel
        cartesianPlane3D.drawAxes();
        cartesianPlane3D.drawCube(cartesianPlane3D.getCubeVertices());
    }
    
    
}
