package project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport;

import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane2D;

import javax.swing.*;
import java.awt.*;

public class ViewportWindow extends JFrame {

    private static final int WINDOW_MARGIN_X = 80;
    private static final int WINDOW_MARGIN_Y = 60;

    private Viewport2D viewport;
    private JPanel viewportPanel;

    public ViewportWindow(int width, int height) {
        setTitle("Viewport Window");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setAlwaysOnTop(true);

        int panelWidth = width + (WINDOW_MARGIN_X * 2);
        int panelHeight = height + (WINDOW_MARGIN_Y * 2);

        // Inicializa a viewport centralizada dentro de uma janela maior.
        viewport = new Viewport2D(WINDOW_MARGIN_X, WINDOW_MARGIN_Y, width, height);

        // Configura o painel de exibição da viewport
        viewportPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                viewport.draw(g);
            }
        };

        viewportPanel.setBackground(Color.DARK_GRAY);
        viewportPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        add(viewportPanel, BorderLayout.CENTER);

        pack();
    }

    public void updateViewport(JPanel plane, int worldXMin, int worldYMin, int worldXMax, int worldYMax) {
        viewport.renderFromCartesian((BaseCartesianPlane2D) plane, worldXMin, worldYMin, worldXMax, worldYMax);
        viewportPanel.repaint();
    }

    public void enableViewport() {
        setAlwaysOnTop(true);
        setVisible(true);
        toFront();
        requestFocus();
    }

    public void disableViewport() {
        setVisible(false);
    }

}
