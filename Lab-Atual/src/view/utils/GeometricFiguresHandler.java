package view.utils;

import project_cg.geometry.figures.BaseFigure;

import javax.swing.*;
import java.util.*;

public class GeometricFiguresHandler {


    public Map<String, List<BaseFigure>> hashFigures;

    public JPanelHandler JPanelHandler;

    public GeometricFiguresHandler(JPanelHandler JPanelHandler) {
        this.hashFigures = new LinkedHashMap<>();
        this.JPanelHandler = JPanelHandler;

        this.setCategorys();
    }

    public void addFigure(BaseFigure figure) {
        List<BaseFigure> figures = getFigures();

        figures.add(figure);
    }

    public void setCategorys() {
        List<String> categories = JPanelHandler.getCategories();

        for (String category : categories) {
            hashFigures.put(category, new ArrayList<>());
        }

    }

    public BaseFigure getFigureByID(String id) {
        List<BaseFigure> figures = getFigures();

        return figures
                .stream()
                .filter(figure -> figure.getID().equals(id))
                .findAny()
                .orElse(null);
    }

    public List<BaseFigure> getFigures() {
        return hashFigures.get(JPanelHandler.getCurrentCategory());
    }

    public void resetFigures() {
        hashFigures.put(JPanelHandler.getCurrentCategory(), new ArrayList<>());
    }

    public JComboBox<String> getComboBoxFigures() {
        List<BaseFigure> figures = getFigures();
        JComboBox<String> comboBox = new JComboBox<>();

        if (figures == null) {
            return comboBox;
        }

        for (BaseFigure figure : figures) {
            comboBox.addItem(figure.getID());
        }

        return comboBox;
    }

}
