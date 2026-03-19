package view;

import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport3d.CartesianPlane3D;
import project_cg.geometry.planeCartesians.cartesiansPlane.recortePlanes.RecorteSutherlandHodgmanLinePlane;
import project_cg.geometry.planeCartesians.cartesiansPlane.recortePlanes.RecorteSutherlandPlane;
import project_cg.inputsPanel.transformations3dinputs.Reflection3DInputs;
import project_cg.inputsPanel.transformations3dinputs.Rotation3DInputs;
import project_cg.inputsPanel.transformations3dinputs.Scale3DInputs;
import project_cg.inputsPanel.transformations3dinputs.Shear3DInputs;
import project_cg.inputsPanel.transformations3dinputs.StartCartesianPlaneInputs;
import project_cg.inputsPanel.transformations3dinputs.Translation3DInputs;
import project_cg.geometry.planeCartesians.cartesiansPlane.CartesianPlane2D;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.CartesianPlane2DWithViewport;
import project_cg.geometry.planeCartesians.cartesiansPlane.cartesianWithViewport.QueuedTransformationsPlane;
import project_cg.inputsPanel.conicSectionsInputs.ConicSectionsInputs;
import project_cg.inputsPanel.recorteInputs.ApplyAlgorithmInputs;
import project_cg.inputsPanel.recorteInputs.ClipVisualizationModeInput;
import project_cg.inputsPanel.recorteInputs.DrawCustomLineInput;
import project_cg.inputsPanel.recorteInputs.DrawLinesInputs;
import project_cg.inputsPanel.recorteInputs.SizeWindowInput;
import project_cg.inputsPanel.primitivesInputs.BezierInputs;
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
import project_cg.geometry.planeCartesians.bases.BaseCartesianPlane;
import project_cg.geometry.figures.BaseFigure;
import project_cg.geometry.figures.Square;
import utils.Constants;
import view.mainScreen.MainScreen;
import utils.BaseJPanel;
import utils.DataOptions;
import utils.GeometricFiguresHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainScreenV2 {

    private static MainScreenV2 instance;

    private final MainScreen mainScreen;
    private final DataOptions dataOptions;

    private final JPanel cartesianContainer;
    private final CardLayout cartesianCards;

    private final JComboBox<String> categoryCombo;

    private final JPanel inputPanelHolder;
    private final JLabel coordinatesLabel;
    private final JLabel queuedTransformationsLabel;
    private final JTextArea pointsLogArea;
    private final JScrollPane pointsLogScroll;
    private final JButton applyQueuedButton;
    private final JButton clearButton;

    public MainScreenV2(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.dataOptions = new DataOptions();

        this.cartesianCards = new CardLayout();
        this.cartesianContainer = new JPanel(cartesianCards);

        this.categoryCombo = new JComboBox<>();
        this.inputPanelHolder = new JPanel();
        this.inputPanelHolder.setLayout(new GridBagLayout());
        this.coordinatesLabel = new JLabel("Coordenadas: Nenhuma");
        this.queuedTransformationsLabel = new JLabel("Transformacoes: Nenhuma");
        this.pointsLogArea = new JTextArea(8, 24);
        this.pointsLogScroll = new JScrollPane(pointsLogArea);
        this.applyQueuedButton = new JButton("Aplicar Transformações");
        this.clearButton = new JButton("Limpar");

        instance = this;

        setupPlanesAndOptions();
        setupMainLayout();
        setupListeners();
        setupInitialState();
    }

    private void setupPlanesAndOptions() {
        CartesianPlane2D primitivaPlane = new CartesianPlane2D();
        CartesianPlane2D bezierPlane = new CartesianPlane2D();
        QueuedTransformationsPlane transformacoesPlane = new QueuedTransformationsPlane();
        CartesianPlane2D conicSectionsPlane = new CartesianPlane2D();
        RecorteSutherlandPlane cohenSutherlandPlane = new RecorteSutherlandPlane();
        RecorteSutherlandHodgmanLinePlane sutherlandHodgmanPlane = new RecorteSutherlandHodgmanLinePlane();
        CartesianPlane3D cartesianPlane3D = new CartesianPlane3D();

        mainScreen.JPanelHandler.addJPanel("Primitivas", primitivaPlane);
        mainScreen.JPanelHandler.addJPanel("Algoritmo de Bezier", bezierPlane);
        mainScreen.JPanelHandler.addJPanel("Transformações", transformacoesPlane);
        mainScreen.JPanelHandler.addJPanel("Seções Cônicas", conicSectionsPlane);
        mainScreen.JPanelHandler.addJPanel("Recorte de Linhas Cohen-Sutherland", cohenSutherlandPlane);
        mainScreen.JPanelHandler.addJPanel("Recorte de Linhas Sutherland-Hodgman", sutherlandHodgmanPlane);
        mainScreen.JPanelHandler.addJPanel("Plano 3D", cartesianPlane3D);

        mainScreen.setGeometricFiguresHandler(new GeometricFiguresHandler(mainScreen.getCartesianPlaneHandler()));

        dataOptions.addOption("Transformações", "Desenhar Quadrado", new CreatePolygonInputs());
        dataOptions.addOption("Transformações", "Aplicar Rotação", new RotationInputs());
        dataOptions.addOption("Transformações", "Aplicar Escala", new ScaleInputs());
        dataOptions.addOption("Transformações", "Aplicar Cisalhamento", new ShearInputs());
        dataOptions.addOption("Transformações", "Aplicar Translação", new TranslationInputs());
        dataOptions.addOption("Transformações", "Aplicar Reflexão", new ReflectionInputs());

        dataOptions.addOption("Primitivas", "DDA", new DDALineInputs());
        dataOptions.addOption("Primitivas", "Equação explicita da circunferência", new CircleExplicitInputs());
        dataOptions.addOption("Primitivas", "Método trigonométrico da circunferência", new TrigonometricCircleInputs());
        dataOptions.addOption("Primitivas", "Ponto médio da circunferência", new MidpointCircleInputs());
        dataOptions.addOption("Primitivas", "Ponto médio das Retas", new MidpointLineInputs());
        dataOptions.addOption("Primitivas", "Ponto médio da Elipse", new MidpointElipseInputs());

        dataOptions.addOption("Seções Cônicas", "Gerar Seção Cônica (Elipse/Parábola/Hipérbole)", new ConicSectionsInputs());

        dataOptions.addOption("Algoritmo de Bezier", "Bezier Cúbica", new BezierInputs());

        dataOptions.addOption("Recorte de Linhas Cohen-Sutherland", "Definir tamanho da viewport", new SizeWindowInput());
        dataOptions.addOption("Recorte de Linhas Cohen-Sutherland", "Gerar linhas aleatorias dentro e fora da viewport", new DrawLinesInputs());
        dataOptions.addOption("Recorte de Linhas Cohen-Sutherland", "Desenhar reta customizada", new DrawCustomLineInput());
        dataOptions.addOption("Recorte de Linhas Cohen-Sutherland", "Modo de visualizacao do recorte", new ClipVisualizationModeInput());
        dataOptions.addOption("Recorte de Linhas Cohen-Sutherland", "Aplicar recorte de Cohen-Sutherland", new ApplyAlgorithmInputs());

        dataOptions.addOption("Recorte de Linhas Sutherland-Hodgman", "Definir tamanho da viewport", new SizeWindowInput());
        dataOptions.addOption("Recorte de Linhas Sutherland-Hodgman", "Gerar linhas aleatorias dentro e fora da viewport", new DrawLinesInputs());
        dataOptions.addOption("Recorte de Linhas Sutherland-Hodgman", "Desenhar reta customizada", new DrawCustomLineInput());
        dataOptions.addOption("Recorte de Linhas Sutherland-Hodgman", "Modo de visualizacao do recorte", new ClipVisualizationModeInput());
        dataOptions.addOption("Recorte de Linhas Sutherland-Hodgman", "Aplicar recorte de Sutherland-Hodgman", new ApplyAlgorithmInputs());

        dataOptions.addOption("Plano 3D", "Iniciar Janela 3D", new StartCartesianPlaneInputs());
        dataOptions.addOption("Plano 3D", "Aplicar uma Rotação", new Rotation3DInputs());
        dataOptions.addOption("Plano 3D", "Aplicar uma Reflexão", new Reflection3DInputs());
        dataOptions.addOption("Plano 3D", "Aplicar uma Escala", new Scale3DInputs());
        dataOptions.addOption("Plano 3D", "Aplicar um Cisalhamento", new Shear3DInputs());
        dataOptions.addOption("Plano 3D", "Aplicar uma Translação", new Translation3DInputs());
    }

    private void setupMainLayout() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.add(new JLabel("Categoria:"));
        topBar.add(categoryCombo);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setPreferredSize(new Dimension(Constants.INPUT_SECTION_WIDTH, Constants.INPUT_SECTION_HEIGHT));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 12));

        JLabel inputsTitle = new JLabel("Inputs da Categoria");

        clearButton.addActionListener(e -> clearCurrentCategory());

        applyQueuedButton.addActionListener(e -> applyQueuedTransformationsForCurrentCategory());
        applyQueuedButton.setVisible(false);

        coordinatesLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 2));
        queuedTransformationsLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 2));
        pointsLogArea.setEditable(false);
        pointsLogArea.setLineWrap(true);
        pointsLogArea.setWrapStyleWord(true);
        pointsLogArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        pointsLogArea.setText("Log de pontos e composicao indisponivel.");
        pointsLogScroll.setBorder(BorderFactory.createTitledBorder("Log de Transformacoes"));
        pointsLogScroll.setPreferredSize(new Dimension(Constants.INPUT_SECTION_WIDTH - 24, 145));

        JPanel actionButtons = new JPanel(new GridLayout(1, 2, 8, 0));
        actionButtons.add(applyQueuedButton);
        actionButtons.add(clearButton);

        JPanel footerButtons = new JPanel();
        footerButtons.setLayout(new BorderLayout(0, 4));

        JPanel footerInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        footerInfo.add(coordinatesLabel);
        footerInfo.add(queuedTransformationsLabel);

        footerButtons.add(footerInfo, BorderLayout.NORTH);
        footerButtons.add(pointsLogScroll, BorderLayout.CENTER);
        footerButtons.add(actionButtons, BorderLayout.SOUTH);

        JScrollPane inputsScroll = new JScrollPane(inputPanelHolder);
        inputsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputsScroll.getVerticalScrollBar().setUnitIncrement(14);

        rightPanel.add(inputsTitle, BorderLayout.NORTH);
        rightPanel.add(inputsScroll, BorderLayout.CENTER);
        rightPanel.add(footerButtons, BorderLayout.SOUTH);

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
            updateFooterButtons(selectedCategory);
            updateQueuedTransformationsSummary();
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
        updateFooterButtons("Primitivas");
        updateQueuedTransformationsSummary();
        repaintCurrentCategory();

        mainScreen.setLocationRelativeTo(null);
        mainScreen.setVisible(true);
    }

    private void showEmptyInputs() {
        inputPanelHolder.removeAll();
        JLabel emptyMessage = new JLabel("Selecione uma categoria para exibir os inputs.");
        emptyMessage.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        inputPanelHolder.add(emptyMessage, gbc);

        inputPanelHolder.revalidate();
        inputPanelHolder.repaint();
    }

    private void showInputsByCategory(String category) {
        inputPanelHolder.removeAll();

        String[] options = dataOptions.getSecondComboBoxOptions(category);
        int columns = Math.max(1, Constants.INPUT_SECTION_COLUMNS);

        for (int index = 0; index < options.length; index++) {
            String option = options[index];
            JPanel panel = dataOptions.getPanelInputsForOption(option);

            JPanel wrapper = new JPanel(new BorderLayout());
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

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = index % columns;
            gbc.gridy = index / columns;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;

            int halfGap = Math.max(2, Constants.INPUT_SECTION_H_GAP / 2);
            if (gbc.gridx == 0) {
                gbc.insets = new Insets(0, 0, Constants.INPUT_SECTION_V_GAP, halfGap);
            } else {
                gbc.insets = new Insets(0, halfGap, Constants.INPUT_SECTION_V_GAP, 0);
            }

            inputPanelHolder.add(wrapper, gbc);
        }

        if (options.length == 0) {
            showEmptyInputs();
            return;
        }

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = (options.length + columns - 1) / columns;
        filler.gridwidth = columns;
        filler.weightx = 1.0;
        filler.weighty = 1.0;
        filler.fill = GridBagConstraints.BOTH;
        inputPanelHolder.add(Box.createVerticalGlue(), filler);

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
        updateQueuedTransformationsSummary();
        repaintCurrentCategory();
    }

    private void updateFooterButtons(String selectedCategory) {
        clearButton.setVisible(true);

        if ("Transformações".equals(selectedCategory)) {
            applyQueuedButton.setText("Aplicar Transformações");
            applyQueuedButton.setVisible(true);
            coordinatesLabel.setVisible(true);
            queuedTransformationsLabel.setVisible(true);
            pointsLogScroll.setVisible(true);
            return;
        }

        if ("Plano 3D".equals(selectedCategory)) {
            applyQueuedButton.setText("Aplicar Transformações 3D");
            applyQueuedButton.setVisible(true);
            coordinatesLabel.setVisible(true);
            queuedTransformationsLabel.setVisible(true);
            pointsLogScroll.setVisible(true);
            return;
        }

        applyQueuedButton.setVisible(false);
        coordinatesLabel.setVisible(false);
        queuedTransformationsLabel.setVisible(false);
        pointsLogScroll.setVisible(false);
    }

    private void applyQueuedTransformationsForCurrentCategory() {
        String selectedCategory = mainScreen.JPanelHandler.getCurrentCategory();

        if ("Transformações".equals(selectedCategory)) {
            applyQueuedTransformations2D();
            return;
        }

        if ("Plano 3D".equals(selectedCategory)) {
            applyQueuedTransformations3D();
        }
    }

    private void applyQueuedTransformations2D() {
        Square square = getSingleTransformationSquare();

        if (square == null) {
            JOptionPane.showMessageDialog(mainScreen, "Desenhe o quadrado de referencia antes de aplicar as transformacoes.");
            return;
        }

        QueuedTransformationsPlane plane = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");

        try {
            plane.applyQueuedTransformations(square);
            mainScreen.updateFigures();
            updateQueuedTransformationsSummary();
        } catch (IllegalStateException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(mainScreen, ex.getMessage());
        }
    }

    private Square getSingleTransformationSquare() {
        if (mainScreen.geometricFiguresHandler.getFigures().size() != 1) {
            return null;
        }

        BaseFigure figure = mainScreen.geometricFiguresHandler.getFigures().get(0);
        if (!(figure instanceof Square)) {
            return null;
        }

        return (Square) figure;
    }

    private void applyQueuedTransformations3D() {
        CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

        try {
            plane3D.applyQueuedTransformations();
            updateQueuedTransformationsSummary();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(mainScreen, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateQueuedTransformationsSummary() {
        String selectedCategory = mainScreen.JPanelHandler.getCurrentCategory();

        if ("Transformações".equals(selectedCategory)) {
            QueuedTransformationsPlane plane2D = (QueuedTransformationsPlane) mainScreen.JPanelHandler.getPanelByCategory("Transformações");
            queuedTransformationsLabel.setText("Transformacoes: " + plane2D.getPendingTransformationsSummary());
            coordinatesLabel.setText("Coordenadas: " + getSquareCoordinatesSummary());
            pointsLogArea.setText(build2DTransformationLog(plane2D));
            return;
        }

        if ("Plano 3D".equals(selectedCategory)) {
            CartesianPlane3D plane3D = (CartesianPlane3D) mainScreen.JPanelHandler.getPanelByCategory("Plano 3D");
            queuedTransformationsLabel.setText("Transformacoes: " + plane3D.getPendingTransformationsSummary());
            coordinatesLabel.setText("Coordenadas: " + getCubeCoordinatesSummary(plane3D));
            pointsLogArea.setText(build3DTransformationLog(plane3D));
            return;
        }

        coordinatesLabel.setText("Coordenadas: Nenhuma");
        queuedTransformationsLabel.setText("Transformacoes: Nenhuma");
        pointsLogArea.setText("Log de pontos e composicao indisponivel para esta categoria.");
    }

    private String build2DTransformationLog(QueuedTransformationsPlane plane2D) {
        StringBuilder builder = new StringBuilder();
        builder.append("[2D] Composicao: ")
                .append(plane2D.getPendingTransformationsSummary())
                .append("\n");

        Square square = getSingleTransformationSquare();
        if (square == null) {
            builder.append("Quadrado: nao encontrado");
            return builder.toString();
        }

        final int[] index = {1};
        square.getVertex(point -> {
            builder.append("V")
                    .append(index[0])
                    .append(": (")
                    .append(formatCoordinate(point.getX()))
                    .append(", ")
                    .append(formatCoordinate(point.getY()))
                    .append(")\n");
            index[0]++;
        });

        return builder.toString().trim();
    }

    private String build3DTransformationLog(CartesianPlane3D plane3D) {
        StringBuilder builder = new StringBuilder();
        builder.append("[3D] Composicao: ")
                .append(plane3D.getPendingTransformationsSummary())
                .append("\n");

        if (plane3D == null || plane3D.getCubeVertices() == null || plane3D.getCubeVertices().length == 0) {
            builder.append("Cubo: nao encontrado");
            return builder.toString();
        }

        for (int i = 0; i < plane3D.getCubeVertices().length; i++) {
            builder.append("P")
                    .append(i + 1)
                    .append(": (")
                    .append(formatCoordinate(plane3D.getCubeVertices()[i].getX()))
                    .append(", ")
                    .append(formatCoordinate(plane3D.getCubeVertices()[i].getY()))
                    .append(", ")
                    .append(formatCoordinate(plane3D.getCubeVertices()[i].getZ()))
                    .append(")\n");
        }

        return builder.toString().trim();
    }

    private String getSquareCoordinatesSummary() {
        Square square = getSingleTransformationSquare();
        if (square == null) {
            return "Quadrado nao encontrado";
        }

        List<String> vertices = new ArrayList<>();
        final int[] index = {1};

        square.getVertex(point -> {
            vertices.add("V" + index[0] + "(" + formatCoordinate(point.getX()) + "," + formatCoordinate(point.getY()) + ")");
            index[0]++;
        });

        if (vertices.isEmpty()) {
            return "Quadrado sem vertices";
        }

        return String.join(" ", vertices);
    }

    private String getCubeCoordinatesSummary(CartesianPlane3D plane3D) {
        if (plane3D == null || plane3D.getCubeVertices() == null || plane3D.getCubeVertices().length == 0) {
            return "Cubo nao encontrado";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < plane3D.getCubeVertices().length; i++) {
            if (i > 0) {
                builder.append(" ");
            }

            builder.append("P")
                    .append(i + 1)
                    .append("(")
                    .append(formatCoordinate(plane3D.getCubeVertices()[i].getX()))
                    .append(",")
                    .append(formatCoordinate(plane3D.getCubeVertices()[i].getY()))
                    .append(",")
                    .append(formatCoordinate(plane3D.getCubeVertices()[i].getZ()))
                    .append(")");
        }

        return builder.toString();
    }

    private String formatCoordinate(double value) {
        return String.format("%.2f", value);
    }

    public static void refreshQueuedTransformationsIndicator() {
        if (instance == null) {
            return;
        }

        instance.updateQueuedTransformationsSummary();
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
