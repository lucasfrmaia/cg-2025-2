package project_cg.inputsPanel.transformations2dinputs;

import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.figures.Square;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import project_cg.transformations2d.Translation;
import utils.ShapePanel;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;

import javax.swing.*;

public class TranslationInputs extends ShapePanel {
    private JTextField translationX, translationY;

    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    @Override
    protected String getLabelButtonCalcular() {
        return "Adicionar Translação";
    }

    @Override
    protected void initializeInputs() {
        translationX = new JTextField(10);
        translationY = new JTextField(10);

        addInputField("Translação X:", translationX);
        addInputField("Translação Y:", translationY);
    }

    @Override
    protected void onCalculate() {
        try {
            int tx = Integer.parseInt(translationX.getText().trim());
            int ty = Integer.parseInt(translationY.getText().trim());

            MainScreen mainScreen = MainScreenSingleton.getMainScreen();
            Square square = getSingleTransformationSquare(mainScreen);

            if (square == null) {
                JOptionPane.showMessageDialog(this, "Desenhe o quadrado de referencia antes de adicionar a translacao.");
                return;
            }

            QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");
            plane.queueTransformation(Translation.getMatrixTranslation(tx, ty));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite valores inteiros validos para translacao X e Y.");
        }
    }

    private Square getSingleTransformationSquare(MainScreen mainScreen) {
        if (mainScreen.geometricFiguresHandler.getFigures().size() != 1) {
            return null;
        }

        BaseFigure figure = mainScreen.geometricFiguresHandler.getFigures().getFirst();
        if (!(figure instanceof Square)) {
            return null;
        }

        return (Square) figure;
    }

}
