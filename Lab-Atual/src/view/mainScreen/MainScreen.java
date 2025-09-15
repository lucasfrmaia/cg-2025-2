package view.mainScreen;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.points.Point2D;
import utils.Constants;
import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane;
import view.utils.JPanelHandler;
import view.utils.GeometricFiguresHandler;

import javax.swing.*;
import java.awt.*;

public class MainScreen extends JFrame {

    private JPanel homePanel;
    private JScrollPane inputsScroll;

    public final JPanelHandler JPanelHandler;

    public GeometricFiguresHandler geometricFiguresHandler;

    public MainScreen(JPanel homePanel) {
        this.JPanelHandler = new JPanelHandler();
        this.homePanel = homePanel;
        this.setResizable(false);

        initializeScreen();
    }

    public JPanelHandler getCartesianPlaneHandler() {
        return JPanelHandler;
    }

    public void setInputs(JPanel inputs) {
        this.inputsScroll = new JScrollPane(inputs);
    }

    public void setGeometricFiguresHandler(GeometricFiguresHandler geometricFiguresHandler) {
        this.geometricFiguresHandler = geometricFiguresHandler;
    }

    public void setLayoutPanel(JPanel mainPanel) {
        // Define layout manager e restrições
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Configura o painel principal para ocupar 90% da altura e 100% da largura
        gbc.gridx = 0; // Primeira coluna
        gbc.gridy = 0; // Primeira linha
        gbc.weightx = 1.0; // 100% da largura
        gbc.weighty = 0.95; // 90% da altura
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Ocupa toda a largura
        add(mainPanel, gbc);

        // Configura inputsScroll para ocupar o restante da altura (10%)
        gbc.gridy = 1; // Segunda linha
        gbc.weighty = 0.05; // 5% da altura
        add(inputsScroll, gbc);
    }

    public void setLayoutPanel() {
        setLayoutPanel(homePanel);
    }

    private void initializeScreen() {
        setTitle("Projeto Computação Gráfica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set modern Nimbus Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Fallback to default if Nimbus not available
            e.printStackTrace();
        }

        setSize(Constants.WIDTH_MAIN_SCREEN, Constants.HEIGHT_MAIN_SCREEN);
        setLocationRelativeTo(null); // Center the window
        getContentPane().setBackground(Constants.BACKGROUND_COLOR);
    }

    public void updateCurrentPanel(String categoryCatersianPlane) {
        this.JPanelHandler.setCurrentCategory(categoryCatersianPlane);
        JPanel currentPanel = JPanelHandler.getCurrentPanel();

        getContentPane().removeAll();
        setLayoutPanel(currentPanel);
        revalidate();
        repaint();
    }

    public void resetCartesianPlaneScreen() {
        this.JPanelHandler.resetCurrentJPanel();
        JPanel currentPanel = this.JPanelHandler.getCurrentPanel();

        getContentPane().removeAll();
        setLayoutPanel(currentPanel);
        revalidate();
        repaint();
    }

    public void resetCartesianPlane() {
        this.resetCartesianPlaneScreen();
        this.geometricFiguresHandler.resetFigures();
    }

    public void updateFigures() {
        BaseCartesianPlane cartesianPlane1 = JPanelHandler.getCurrentCartesianPlane();
        cartesianPlane1.clear();

        for(BaseFigure figure : geometricFiguresHandler.getFigures()) {
            for(Point2D point2D : figure.getPoints()) {
                cartesianPlane1.setPixel(point2D, figure.getColor());
            }
        }

        cartesianPlane1.repaint();
    }


}
