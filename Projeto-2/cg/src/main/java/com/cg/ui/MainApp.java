package com.cg.ui;

import com.cg.core.ImageFilter;
import com.cg.core.ImageOperations;
import com.cg.core.filters.*;
import com.cg.model.PGMImage;
import com.cg.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainApp extends JFrame {
    private final PGMImage imgA = new PGMImage();
    private final PGMImage imgB = new PGMImage();
    private PGMImage processedImg = new PGMImage();

    private final ImageCanvas canvasA;
    private final ImageCanvas canvasB;
    private final ImageCanvas canvasProcessed;

    private final String[] paths = {
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/lena.pgm",
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/Airplane.pgm",
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/Lenag.pgm",
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/Lenasalp.pgm",
        // "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/crianca.pgm",
        // "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/atual.pgm"
    };

    private final Runnable[] tabActions = new Runnable[6];

    public MainApp() {
        setTitle("Processador de Imagens - Projeto CG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel canvasesPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        canvasesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        canvasA = new ImageCanvas(256, 256);
        canvasB = new ImageCanvas(256, 256);
        canvasProcessed = new ImageCanvas(256, 256);

        canvasesPanel.add(canvasA);
        canvasesPanel.add(canvasB);
        canvasesPanel.add(canvasProcessed);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Req 1: Filtros e Operações", createReq1Tab());
        tabbedPane.addTab("Req 2: Morfismo", createReq2Tab());
        tabbedPane.addTab("Req 3: Transformações", createReq3Tab());
        tabbedPane.addTab("Req 4: Equalização (Histograma)", createReq4Tab());
        tabbedPane.addTab("Req 5: Morfologia", createReq5Tab());
        tabbedPane.addTab("Req 6: Geometria", createReq6Tab());

        tabbedPane.addChangeListener(e -> {
            clearCanvases();
            int idx = tabbedPane.getSelectedIndex();
            if (tabActions[idx] != null) {
                tabActions[idx].run();
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(tabbedPane, BorderLayout.CENTER);

        add(canvasesPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        if (tabActions[0] != null) {
            tabActions[0].run();
        }
    }

    private JPanel createReq1Tab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        String[] filters = {
            "Soma", "Subtração", "Multiplicação", "Divisão", "OR", "AND", "XOR",
            "Média", "Mediana", "Passa-Altas", "Aguçamento", "Prewitt", "Sobel", "Roberts", "Alto Reforço"
        };
        JComboBox<String> cbFilter = new JComboBox<>(filters);
        JCheckBox chkNormalize = new JCheckBox("Normalizar Saída", true);

        Runnable updateAction = () -> {
            String selected = (String) cbFilter.getSelectedItem();
            boolean requiresTwo = false;
            ImageFilter filter = null;
            
            if (selected.equals("Média")) {
                loadImg(imgA, canvasA, 2);
                clearCanvas(canvasB);
                filter = new MeanFilter();
            } else if (selected.equals("Mediana")) {
                loadImg(imgA, canvasA, 3);
                clearCanvas(canvasB);
                filter = new MedianFilter();
            } else {
                loadImg(imgA, canvasA, 0);
                
                boolean norm = chkNormalize.isSelected();
                switch (selected) {
                    case "Soma": filter = new ArithmeticFilter(ImageOperations.ADD::apply, norm); requiresTwo = true; break;
                    case "Subtração": filter = new ArithmeticFilter(ImageOperations.SUB::apply, norm); requiresTwo = true; break;
                    case "Multiplicação": filter = new ArithmeticFilter(ImageOperations.MUL::apply, norm); requiresTwo = true; break;
                    case "Divisão": filter = new ArithmeticFilter(ImageOperations.DIVIDE::apply, norm); requiresTwo = true; break;
                    case "OR": filter = new ArithmeticFilter(ImageOperations.OR::apply, norm); requiresTwo = true; break;
                    case "AND": filter = new ArithmeticFilter(ImageOperations.AND::apply, norm); requiresTwo = true; break;
                    case "XOR": filter = new ArithmeticFilter(ImageOperations.XOR::apply, norm); requiresTwo = true; break;
                    case "Passa-Altas": filter = new HighPassFilter(); break;
                    case "Aguçamento": filter = new SharpenFilter(); break;
                    case "Prewitt": filter = new PrewittFilter(); break;
                    case "Sobel": filter = new SobelFilter(); break;
                    case "Roberts": filter = new RobertsFilter(); break;
                    case "Alto Reforço": filter = new HighBoostFilter(); break;
                }
                
                if (requiresTwo) {
                    loadImg(imgB, canvasB, 1);
                } else {
                    clearCanvas(canvasB);
                }
            }
            
            applyFilter(filter, requiresTwo);
        };

        tabActions[0] = updateAction;
        cbFilter.addActionListener(e -> updateAction.run());
        chkNormalize.addActionListener(e -> updateAction.run());

        panel.add(new JLabel("Selecione a Operação:"));
        panel.add(cbFilter);
        panel.add(chkNormalize);

        return panel;
    }

    private JPanel createReq2Tab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        JSlider timeSlider = new JSlider(0, 100, 50);
        timeSlider.setMajorTickSpacing(25);
        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);

        Runnable updateAction = () -> {
            loadImg(imgA, canvasA, 4); 
            loadImg(imgB, canvasB, 5); 
            
            double t = timeSlider.getValue() / 100.0;
            applyFilter(new MorphingFilter(t), true);
        };

        tabActions[1] = updateAction;
        timeSlider.addChangeListener(e -> updateAction.run());

        panel.add(new JLabel("Transição de Tempo (t):"));
        panel.add(timeSlider);

        return panel;
    }

    private JPanel createReq3Tab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        String[] filters = {"Negativo", "Gama (0.5)", "Logarítmica", "Sigmoide", "Faixa Dinâmica", "Linear"};
        JComboBox<String> cbFilter = new JComboBox<>(filters);

        Runnable updateAction = () -> {
            loadImg(imgA, canvasA, 0);
            clearCanvas(canvasB);
            
            ImageFilter filter = null;
            switch ((String) cbFilter.getSelectedItem()) {
                case "Negativo": filter = new NegativeFilter(); break;
                case "Gama (0.5)": filter = new GammaFilter(0.5); break;
                case "Logarítmica": filter = new LogarithmicFilter(); break;
                case "Sigmoide": filter = new SigmoidFilter(127.0, 25.0); break;
                case "Faixa Dinâmica": filter = new DynamicRangeFilter(); break;
                case "Linear": filter = new LinearTransformFilter(1.2, 20); break;
            }
            applyFilter(filter, false);
        };

        tabActions[2] = updateAction;
        cbFilter.addActionListener(e -> updateAction.run());

        panel.add(new JLabel("Transformação de Intensidade:"));
        panel.add(cbFilter);

        return panel;
    }

    private JPanel createReq4Tab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton btnPlot = new JButton("Visualizar Histogramas");

        Runnable updateAction = () -> {
            loadImg(imgA, canvasA, 0);
            clearCanvas(canvasB);
            applyFilter(new HistogramEqualizationFilter(), false);
        };

        tabActions[3] = updateAction;

        btnPlot.addActionListener(e -> {
            if (imgA.data != null && processedImg.data != null) {
                showHistogramsDialog(imgA, processedImg);
            }
        });

        panel.add(new JLabel("Equalização de Histograma (Lena)"));
        panel.add(btnPlot);

        return panel;
    }

    private JPanel createReq5Tab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.add(new JLabel("Operadores Morfológicos serão implementados aqui."));
        tabActions[4] = () -> clearCanvases();
        return panel;
    }

    private JPanel createReq6Tab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.add(new JLabel("Transformações Geométricas serão implementadas aqui."));
        tabActions[5] = () -> clearCanvases();
        return panel;
    }

    private void loadImg(PGMImage target, ImageCanvas canvas, int pathIndex) {
        ImageUtils.readImage(paths[pathIndex], target);
        canvas.setImage(target);
        canvas.repaint();
    }

    private void clearCanvas(ImageCanvas canvas) {
        canvas.setImage(new PGMImage());
        canvas.repaint();
    }

    private void clearCanvases() {
        clearCanvas(canvasA);
        clearCanvas(canvasB);
        clearCanvas(canvasProcessed);
        imgA.data = null;
        imgB.data = null;
        processedImg.data = null;
    }

    private void applyFilter(ImageFilter filter, boolean requiresTwo) {
        if (filter == null || imgA.data == null) return;

        if (requiresTwo && imgB.data != null) {
            processedImg = filter.apply(imgA, imgB);
        } else if (!requiresTwo) {
            processedImg = filter.apply(imgA);
        }

        if (processedImg != null && processedImg.data != null) {
            canvasProcessed.setImage(processedImg);
            canvasProcessed.repaint();
        }
    }

    private void showHistogramsDialog(PGMImage original, PGMImage equalized) {
        JDialog dialog = new JDialog(this, "Histogramas - Original vs Equalizada", true);
        dialog.setLayout(new GridLayout(1, 2));

        dialog.add(createHistogramPanel("Histograma Original", original));
        dialog.add(createHistogramPanel("Histograma Equalizado", equalized));

        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createHistogramPanel(String title, PGMImage img) {
        int[] hist = new int[256];
        int maxVal = 0;
        for (int val : img.data) {
            hist[val]++;
            if (hist[val] > maxVal) maxVal = hist[val];
        }

        final int finalMaxVal = maxVal;

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(Color.BLACK);
                int padding = 30;
                int width = getWidth() - 2 * padding;
                int height = getHeight() - 2 * padding;

                for (int i = 0; i < 256; i++) {
                    int barHeight = (int) (((double) hist[i] / finalMaxVal) * height);
                    int x = padding + (int) ((i / 256.0) * width);
                    int y = getHeight() - padding - barHeight;
                    g2d.drawLine(x, getHeight() - padding, x, y);
                }
            }
        };
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}