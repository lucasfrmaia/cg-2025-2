package view.mainScreen.mainScreenPanels;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.transformations3dinputs.*;
import project_cg.ecgSimulator.ECGSimulation;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.CartesianPlane2DWithViewport;
import project_cg.inputsPanel.ecgInputs.ECGSimulationInputs;
import view.mainScreen.MainScreen;
import view.utils.DataOptions;
import project_cg.inputsPanel.primitivesInputs.*;
import project_cg.inputsPanel.transformations2dinputs.*;
import project_cg.geometry.planeCartesians.cartesiansPlane.CartesianPlane2D;
import project_cg.geometry.planeCartesians.cartesiansPlane.PixelCartesianPlane;
import view.select.SelectOptions;
import view.mainScreen.MainScreenSingleton;
import view.utils.GeometricFiguresHandler;

import javax.swing.*;
import java.awt.*;

public class InputsPanel extends JPanel {
    private JButton clearButton = new JButton("Limpar");

    public InputsPanel() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();

        setLayout(new BorderLayout());

        DataOptions dataOptions = new DataOptions();

        // Criação de novas instâncias de CartesianPlane2D e PixelCartesianPlane
        CartesianPlane2D primitivaPlane = new CartesianPlane2D();
        CartesianPlane2DWithViewport transformacoesPlane = new CartesianPlane2DWithViewport();

        PixelCartesianPlane pixelPlane = new PixelCartesianPlane();
        ECGSimulation ecgSimulation = new ECGSimulation();
        CartesianPlane3D cartesianPlane3D = new CartesianPlane3D();

        // Main3DViewer main3DViewer = new Main3DViewer();

        // Adicionando os planos cartesianos
        mainScreen.JPanelHandler.addJPanel("Primitivas", primitivaPlane);
        mainScreen.JPanelHandler.addJPanel("Transformações", transformacoesPlane);
        mainScreen.JPanelHandler.addJPanel("Pixel", pixelPlane);
        mainScreen.JPanelHandler.addJPanel("Simulador de Coracão", ecgSimulation);
        mainScreen.JPanelHandler.addJPanel("Plano 3D", cartesianPlane3D);

        // ToDo fluxo, não mexer p n estourar o codigo
        mainScreen.setGeometricFiguresHandler(new GeometricFiguresHandler(mainScreen.getCartesianPlaneHandler()));

        // Criação de instâncias de inputs para transformações
        CreatePolygonInputs polygonInputs = new CreatePolygonInputs();
        RotationInputs rotationInputs = new RotationInputs();
        ScaleInputs scaleInputs = new ScaleInputs();
        ShearInputs shearInputs = new ShearInputs();
        TranslationInputs translationInputs = new TranslationInputs();
        ReflectionInputs reflectionInputs = new ReflectionInputs();

        // Criação de instâncias de inputs para primitivas
        DDALineInputs ddaLineInputs = new DDALineInputs();
        CircleExplicitInputs circleExplicitInputs = new CircleExplicitInputs();
        TrigonometricCircleInputs trigonometricCircleInputs = new TrigonometricCircleInputs();
        MidpointCircleInputs midpointCircleInputs = new MidpointCircleInputs();
        MidpointLineInputs midpointLineInputs = new MidpointLineInputs();
        MidpointElipseInputs midpointElipseInputs = new MidpointElipseInputs();

        // Input do ECG
        ECGSimulationInputs ecgSimulationInputs = new ECGSimulationInputs();

        //dataOptions.addOption("Transformações", "Desenhar Reta", midpointLineInputs);

        dataOptions.addOption("Transformações", "Desenhar Quadrado", polygonInputs);
        dataOptions.addOption("Transformações", "Aplicar Rotação", rotationInputs);
        dataOptions.addOption("Transformações", "Aplicar Escala", scaleInputs);
        dataOptions.addOption("Transformações", "Aplicar Cisalhamento", shearInputs);
        dataOptions.addOption("Transformações", "Aplicar Translação", translationInputs);
        dataOptions.addOption("Transformações", "Aplicar Reflexão", reflectionInputs);

        // Adicionando as opções de primitivas
        dataOptions.addOption("Primitivas", "DDA", ddaLineInputs);
        dataOptions.addOption("Primitivas", "Equação explicita da circunferência", circleExplicitInputs);
        dataOptions.addOption("Primitivas", "Método trigonométrico da circunferência", trigonometricCircleInputs);
        dataOptions.addOption("Primitivas", "Ponto médio da circunferência", midpointCircleInputs);
        dataOptions.addOption("Primitivas", "Ponto médio das Retas", midpointLineInputs);
        dataOptions.addOption("Primitivas", "Ponto médio da Elipse", midpointElipseInputs);

        // Opções do ECG
        dataOptions.addOption("Simulador de Coracão", "Definir Tempo", ecgSimulationInputs);

        // Transformácões 3d
        dataOptions.addOption("Plano 3D",  "Iniciar Janela 3D", new StartCartesianPlaneInputs());
        dataOptions.addOption("Plano 3D",  "Criar Cubo", new CreateCubeInputs());
        dataOptions.addOption("Plano 3D",  "Aplicar uma Rotação", new Rotation3DInputs());
        dataOptions.addOption("Plano 3D",  "Aplicar uma Reflexão", new Reflection3DInputs());
        dataOptions.addOption("Plano 3D",  "Aplicar uma Escala", new Scale3DInputs());
        dataOptions.addOption("Plano 3D",  "Aplicar um Cisalhamento", new Shear3DInputs());
        dataOptions.addOption("Plano 3D",  "Aplicar uma Translação", new Translation3DInputs());


        add(new SelectOptions(dataOptions), BorderLayout.CENTER);
        add(clearButton, BorderLayout.SOUTH);

        clearButton.addActionListener(e-> {
             mainScreen.resetCartesianPlane();
        });

        setVisible(true);
    }


}