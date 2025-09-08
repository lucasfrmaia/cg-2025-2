package view.utils;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.ecgSimulator.ECGSimulation;
import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.CartesianPlane2DWithViewport;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JPanelHandler {

    private final Map<String, BaseJPanel> cartesiansPlane;

    private String currentCartesianPlaneString;

    public JPanelHandler() {
        this.cartesiansPlane = new LinkedHashMap<>();
    }

    public String getCurrentCategory () {
        return currentCartesianPlaneString;
    }

    public void setCurrentCategory(String currentCartesianPlaneString) {
        this.currentCartesianPlaneString = currentCartesianPlaneString;
    }

    public List<String> getCategories() {
        return new ArrayList<>(cartesiansPlane.keySet().stream().toList());
    }

    public BaseJPanel getPanelByCategory(String category) {
        return cartesiansPlane.get(category);
    }

    public BaseJPanel getCurrentPanel() {
        return cartesiansPlane.get(currentCartesianPlaneString);
    }

    public void addJPanel(String category, BaseJPanel jPanel) {
        cartesiansPlane.put(category, jPanel);
    }

    public void resetCurrentJPanel() {
        BaseJPanel currentCartesianPlane = getCurrentPanel();

        this.cartesiansPlane.put(
                currentCartesianPlaneString,
                currentCartesianPlane.reset()
        );
    }

    // Plano 3D
    public CartesianPlane3D getCartesianPlane3D() {
        JPanel currentPanel = cartesiansPlane.get(currentCartesianPlaneString);

        if (currentPanel instanceof CartesianPlane3D) {
            return (CartesianPlane3D) currentPanel;
        }

        throw new RuntimeException("Função chamada indevidamente (Plano 3D)");
    }

    // obter ECG Simulator
    public ECGSimulation getECGSimulation() {
        return (ECGSimulation) getCurrentPanel();
    }


    // Manipular os planos cartesianos
    public CartesianPlane2DWithViewport getCurrentCartesianPlaneWiewport() {
        JPanel currentPanel = cartesiansPlane.get(currentCartesianPlaneString);

        if (currentPanel instanceof CartesianPlane2DWithViewport) {
            return (CartesianPlane2DWithViewport) currentPanel;
        }

        throw new RuntimeException("Função chamada indevidamente");
    }

    public BaseCartesianPlane getCurrentCartesianPlane() {
        JPanel currentPanel = cartesiansPlane.get(currentCartesianPlaneString);

        if (currentPanel instanceof BaseCartesianPlane) {
            return (BaseCartesianPlane) currentPanel;
        }

        throw new RuntimeException("Função chamada indevidamente");
    }


}
