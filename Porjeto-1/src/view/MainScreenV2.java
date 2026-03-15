package view;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import project_cg.drivers.tudo3D.transformations3dinputs.Reflection3DInputs;
import project_cg.drivers.tudo3D.transformations3dinputs.Rotation3DInputs;
import project_cg.drivers.tudo3D.transformations3dinputs.Scale3DInputs;
import project_cg.drivers.tudo3D.transformations3dinputs.Shear3DInputs;
import project_cg.drivers.tudo3D.transformations3dinputs.StartCartesianPlaneInputs;
import project_cg.drivers.tudo3D.transformations3dinputs.Translation3DInputs;
import project_cg.drivers.tudo3D.transformations3dinputs.ApplyQueuedTransformations3DInputs;
import project_cg.geometry.planeCartesians.cartesiansPlane.CartesianPlane2D;
import project_cg.geometry.planeCartesians.cartesiansPlane.PixelCartesianPlane;
import project_cg.geometry.planeCartesians.cartesiansPlane.RecorteSutherlandPlane;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.CartesianPlane2DWithViewport;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import project_cg.inputsPanel.recorteInputs.ApplyAlgorithmInputs;
import project_cg.inputsPanel.recorteInputs.DrawCustomLineInput;
import project_cg.inputsPanel.recorteInputs.DrawLinesInputs;
import project_cg.inputsPanel.recorteInputs.SizeWindowInput;
import project_cg.inputsPanel.primitivesInputs.CircleExplicitInputs;
import project_cg.inputsPanel.primitivesInputs.DDALineInputs;
import project_cg.inputsPanel.primitivesInputs.MidpointCircleInputs;
import project_cg.inputsPanel.primitivesInputs.MidpointElipseInputs;
import project_cg.inputsPanel.primitivesInputs.MidpointLineInputs;
import project_cg.inputsPanel.primitivesInputs.TrigonometricCircleInputs;
import project_cg.inputsPanel.transformations2dinputs.CreatePolygonInputs;
import project_cg.inputsPanel.transformations2dinputs.ReflectionInputs;
import project_cg.inputsPanel.transformations2dinputs.RotationInputs;
import project_cg.inputsPanel.transformations2dinputs.ScaleInputs;
import project_cg.inputsPanel.transformations2dinputs.ShearInputs;
import project_cg.inputsPanel.transformations2dinputs.TranslationInputs;
import project_cg.inputsPanel.transformations2dinputs.ApplyQueuedTransformationsInputs;
import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane;
import utils.Constants;
import view.mainScreen.MainScreen;
import utils.BaseJPanel;
import utils.DataOptions;
import utils.GeometricFiguresHandler;

import javax.swing.*;
import java.awt.*;

public class MainScreenV2 {

    private final MainScreen mainScreen;
    private final DataOptions dataOptions;

    private final JPanel cartesianContainer;
    private final CardLayout cartesianCards;

    private final JComboBox<String> categoryCombo;

    private final JPanel inputPanelHolder;

    public MainScreenV2(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.dataOptions = new DataOptions();

        this.cartesianCards = new CardLayout();
        this.cartesianContainer = new JPanel(cartesianCards);

        this.categoryCombo = new JComboBox<>();
        this.inputPanelHolder = new JPanel();
        this.inputPanelHolder.setLayout(new BoxLayout(this.inputPanelHolder, BoxLayout.Y_AXIS));

        setupPlanesAndOptions();
        setupMainLayout();
        setupListeners();
        setupInitialState();
    }

    private void setupPlanesAndOptions() {
        CartesianPlane2D primitivaPlane = new CartesianPlane2D();
        QueuedTransformationsPlane transformacoesPlane = new QueuedTransformationsPlane();
        PixelCartesianPlane pixelPlane = new PixelCartesianPlane();
        RecorteSutherlandPlane recortePlane = new RecorteSutherlandPlane();
        CartesianPlane3D cartesianPlane3D = new CartesianPlane3D();

        mainScreen.JPanelHandler.addJPanel("Primitivas", primitivaPlane);
        mainScreen.JPanelHandler.addJPanel("Transformações", transformacoesPlane);
        mainScreen.JPanelHandler.addJPanel("Pixel", pixelPlane);
        mainScreen.JPanelHandler.addJPanel("Recorte de Janela de Cohen-Sutherland", recortePlane);
        mainScreen.JPanelHandler.addJPanel("Plano 3D", cartesianPlane3D);

        mainScreen.setGeometricFiguresHandler(new GeometricFiguresHandler(mainScreen.getCartesianPlaneHandler()));

        dataOptions.addOption("Transformações", "Desenhar Quadrado", new CreatePolygonInputs());
        dataOptions.addOption("Transformações", "Aplicar Rotação", new RotationInputs());
        dataOptions.addOption("Transformações", "Aplicar Escala", new ScaleInputs());
        dataOptions.addOption("Transformações", "Aplicar Cisalhamento", new ShearInputs());
        dataOptions.addOption("Transformações", "Aplicar Translação", new TranslationInputs());
        dataOptions.addOption("Transformações", "Aplicar Reflexão", new ReflectionInputs());
        dataOptions.addOption("Transformações", "Aplicar Transformações Acumuladas", new ApplyQueuedTransformationsInputs());

        dataOptions.addOption("Primitivas", "DDA", new DDALineInputs());
        dataOptions.addOption("Primitivas", "Equação explicita da circunferência", new CircleExplicitInputs());
        dataOptions.addOption("Primitivas", "Método trigonométrico da circunferência", new TrigonometricCircleInputs());
        dataOptions.addOption("Primitivas", "Ponto médio da circunferência", new MidpointCircleInputs());
        dataOptions.addOption("Primitivas", "Ponto médio das Retas", new MidpointLineInputs());
        dataOptions.addOption("Primitivas", "Ponto médio da Elipse", new MidpointElipseInputs());

        dataOptions.addOption("Recorte de Janela de Cohen-Sutherland", "Definir tamanho da viewport", new SizeWindowInput());
        dataOptions.addOption("Recorte de Janela de Cohen-Sutherland", "Gerar linhas aleatorias dentro e fora da viewport", new DrawLinesInputs());
        dataOptions.addOption("Recorte de Janela de Cohen-Sutherland", "Desenhar reta customizada", new DrawCustomLineInput());
        dataOptions.addOption("Recorte de Janela de Cohen-Sutherland", "Aplicar recorte de Cohen-Sutherland", new ApplyAlgorithmInputs());

        dataOptions.addOption("Plano 3D", "Iniciar Janela 3D", new StartCartesianPlaneInputs());
        dataOptions.addOption("Plano 3D", "Aplicar uma Rotação", new Rotation3DInputs());
        dataOptions.addOption("Plano 3D", "Aplicar uma Reflexão", new Reflection3DInputs());
        dataOptions.addOption("Plano 3D", "Aplicar uma Escala", new Scale3DInputs());
        dataOptions.addOption("Plano 3D", "Aplicar um Cisalhamento", new Shear3DInputs());
        dataOptions.addOption("Plano 3D", "Aplicar uma Translação", new Translation3DInputs());
        dataOptions.addOption("Plano 3D", "Aplicar Transformacoes 3D Acumuladas", new ApplyQueuedTransformations3DInputs());
    }

    private void setupMainLayout() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.add(new JLabel("Categoria:"));
        topBar.add(categoryCombo);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setPreferredSize(new Dimension(380, Constants.HEIGHT_MAIN_SCREEN));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 12));

        JLabel inputsTitle = new JLabel("Inputs da Categoria");

        JButton clearButton = new JButton("Limpar");
        clearButton.addActionListener(e -> clearCurrentCategory());

        JScrollPane inputsScroll = new JScrollPane(inputPanelHolder);
        inputsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputsScroll.getVerticalScrollBar().setUnitIncrement(14);

        rightPanel.add(inputsTitle, BorderLayout.NORTH);
        rightPanel.add(inputsScroll, BorderLayout.CENTER);
        rightPanel.add(clearButton, BorderLayout.SOUTH);

        root.add(topBar, BorderLayout.NORTH);
        root.add(cartesianContainer, BorderLayout.CENTER);
        root.add(rightPanel, BorderLayout.EAST);

        mainScreen.getContentPane().removeAll();
        mainScreen.setLayout(new BorderLayout());
        mainScreen.add(root, BorderLayout.CENTER);
    }

    private void setupListeners() {
        categoryCombo.addActionListener(e -> {
            String selectedCategory = (String) categoryCombo.getSelectedItem();

            if (selectedCategory == null) {
                showEmptyInputs();
                return;
            }

            mainScreen.JPanelHandler.setCurrentCategory(selectedCategory);
            cartesianCards.show(cartesianContainer, selectedCategory);

            showInputsByCategory(selectedCategory);
            updateViewportVisibility(selectedCategory);
            repaintCurrentCategory();
        });
    }

    private void setupInitialState() {
        for (String category : dataOptions.getFirstComboBoxOptions()) {
            categoryCombo.addItem(category);

            BaseJPanel panel = mainScreen.JPanelHandler.getPanelByCategory(category);
            cartesianContainer.add(panel, category);
        }

        categoryCombo.setSelectedItem("Primitivas");
        mainScreen.JPanelHandler.setCurrentCategory("Primitivas");
        cartesianCards.show(cartesianContainer, "Primitivas");
        showInputsByCategory("Primitivas");
        updateViewportVisibility("Primitivas");
        repaintCurrentCategory();

        mainScreen.setLocationRelativeTo(null);
        mainScreen.setVisible(true);
    }

    private void showEmptyInputs() {
        inputPanelHolder.removeAll();
        JLabel emptyMessage = new JLabel("Selecione uma categoria para exibir os inputs.");
        emptyMessage.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        inputPanelHolder.add(emptyMessage);
        inputPanelHolder.revalidate();
        inputPanelHolder.repaint();
    }

    private void showInputsByCategory(String category) {
        inputPanelHolder.removeAll();

        String[] options = dataOptions.getSecondComboBoxOptions(category);

        for (String option : options) {
            JPanel panel = dataOptions.getPanelInputsForOption(option);

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrapper.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createTitledBorder(option),
                        BorderFactory.createEmptyBorder(6, 3, 6, 6)
                    )
            );

            if (panel != null) {
                panel.setBorder(BorderFactory.createEmptyBorder(2, 1, 4, 4));
                wrapper.add(panel, BorderLayout.CENTER);
            }

            inputPanelHolder.add(wrapper);
            inputPanelHolder.add(Box.createVerticalStrut(10));
        }

        if (options.length == 0) {
            showEmptyInputs();
            return;
        }

        inputPanelHolder.revalidate();
        inputPanelHolder.repaint();
    }

    private void updateViewportVisibility(String selectedCategory) {
        BaseJPanel panel = mainScreen.JPanelHandler.getPanelByCategory("Transformações");

        if (panel instanceof CartesianPlane2DWithViewport) {
            CartesianPlane2DWithViewport transformacoesPlane = (CartesianPlane2DWithViewport) panel;

            if ("Transformações".equals(selectedCategory)) {
                transformacoesPlane.viewportWindow.enableViewport();
            } else {
                transformacoesPlane.viewportWindow.disableViewport();
            }
        }
    }

    private void clearCurrentCategory() {
        String selectedCategory = mainScreen.JPanelHandler.getCurrentCategory();

        if (selectedCategory == null || selectedCategory.isBlank()) {
            return;
        }

        mainScreen.JPanelHandler.resetCurrentJPanel();
        mainScreen.geometricFiguresHandler.resetFigures();
        refreshCartesianCards();
        repaintCurrentCategory();
    }

    private void refreshCartesianCards() {
        cartesianContainer.removeAll();

        for (String category : dataOptions.getFirstComboBoxOptions()) {
            BaseJPanel panel = mainScreen.JPanelHandler.getPanelByCategory(category);
            cartesianContainer.add(panel, category);
        }

        String selectedCategory = mainScreen.JPanelHandler.getCurrentCategory();
        if (selectedCategory != null) {
            cartesianCards.show(cartesianContainer, selectedCategory);
        }

        cartesianContainer.revalidate();
        cartesianContainer.repaint();
    }

    private void repaintCurrentCategory() {
        BaseJPanel currentPanel = mainScreen.JPanelHandler.getCurrentPanel();

        if (currentPanel instanceof BaseCartesianPlane) {
            mainScreen.updateFigures();
            return;
        }

        currentPanel.repaint();
    }
}
