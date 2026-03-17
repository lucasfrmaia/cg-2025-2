package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.points.Point2D;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import project_cg.transformations2d.Reflection;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import java.util.Objects;
import java.util.function.Function;

public class ReflectionInputs extends ShapePanel {
    private JComboBox<String> reflectionTypeComboBox;
    private JComboBox<String> comboBoxFigures;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Reflexão";
    }


    @Override
    protected void initializeInputs() {
        reflectionTypeComboBox = new JComboBox<>(new String[]{"X", "Y", "Origem"});
        comboBoxFigures = MainScreenSingleton.getComboBoxGeometriFigures();

        addComboBox("Escolha uma figura", comboBoxFigures);
        addComboBox("Tipo de Reflexão:", reflectionTypeComboBox);
    }

    @Override
    protected void onCalculate() {
        String reflectionType = (String) reflectionTypeComboBox.getSelectedItem();

        MainScreen mainScreen = MainScreenSingleton.getMainScreen();

        Function<Point2D, Point2D> reflectionFunnction = switch (Objects.requireNonNull(reflectionType)) {
            case "X" -> Reflection::reflectPpintInX;
            case "Y" -> Reflection::reflectPpintInY;
            case "Origem" -> Reflection::reflectPpintInOrigin;
            default -> null;
        };

        String squareSelected = (String) comboBoxFigures.getSelectedItem();
        BaseFigure figure = mainScreen.geometricFiguresHandler.getFigureByID(squareSelected);

        if (figure == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma figura valida para adicionar a reflexao.");
            return;
        }

        QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");

        assert reflectionFunnction != null;
        plane.queueTransformation(figure.getID(), reflectionFunnction::apply);
    }
}


