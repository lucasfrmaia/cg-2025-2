package view.utils;


import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane;

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


    public BaseCartesianPlane getCurrentCartesianPlane() {
        JPanel currentPanel = cartesiansPlane.get(currentCartesianPlaneString);

        if (currentPanel instanceof BaseCartesianPlane) {
            return (BaseCartesianPlane) currentPanel;
        }

        throw new RuntimeException("Função chamada indevidamente");
    }


}
