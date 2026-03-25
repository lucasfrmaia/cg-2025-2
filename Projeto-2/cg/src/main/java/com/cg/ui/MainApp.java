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

    private final String[] imgNamesDefault = {"Lena", "Airplane"};
    private final Runnable[] tabActions = new Runnable[5];

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
        tabbedPane.addTab("Aritmética e Lógica", createArithmeticsTab());
        tabbedPane.addTab("Realce e Bordas", createEnhancementTab());
        tabbedPane.addTab("Suavização", createSmoothingTab());
        tabbedPane.addTab("Transformações", createIntensityTab());
        tabbedPane.addTab("Morfismo Temporal", createMorphingTab());

        tabbedPane.addChangeListener(e -> {
            clearCanvases();
            int idx = tabbedPane.getSelectedIndex();
            if (tabActions[idx] != null) {
                tabActions[idx].run();
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(tabbedPane, BorderLayout.CENTER);

        JButton downloadBtn = new JButton("Fazer Download da Imagem Processada");
        downloadBtn.setPreferredSize(new Dimension(0, 40));
        downloadBtn.addActionListener(e -> saveProcessedImage());
        bottomPanel.add(downloadBtn, BorderLayout.SOUTH);

        add(canvasesPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        if (tabActions[0] != null) {
            tabActions[0].run();
        }
    }

    private JPanel createArithmeticsTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        JComboBox<String> cbImgA = new JComboBox<>(imgNamesDefault);
        JComboBox<String> cbImgB = new JComboBox<>(imgNamesDefault);
        cbImgB.setSelectedIndex(1);
        
        String[] filters = {"Soma", "Subtração", "Multiplicação", "Divisão", "OR", "AND", "XOR"};
        JComboBox<String> cbFilter = new JComboBox<>(filters);
        
        JCheckBox chkNormalize = new JCheckBox("Normalizar Saída", true);

        Runnable updateAction = () -> {
            loadImg(imgA, canvasA, cbImgA.getSelectedIndex());
            loadImg(imgB, canvasB, cbImgB.getSelectedIndex());
            
            boolean norm = chkNormalize.isSelected();
            ImageFilter filter = null;
            
            switch ((String) cbFilter.getSelectedItem()) {
                case "Soma": filter = new ArithmeticFilter(ImageOperations.ADD::apply, norm); break;
                case "Subtração": filter = new ArithmeticFilter(ImageOperations.SUB::apply, norm); break;
                case "Multiplicação": filter = new ArithmeticFilter(ImageOperations.MUL::apply, norm); break;
                case "Divisão": filter = new ArithmeticFilter(ImageOperations.DIVIDE::apply, norm); break;
                case "OR": filter = new ArithmeticFilter(ImageOperations.OR::apply, norm); break;
                case "AND": filter = new ArithmeticFilter(ImageOperations.AND::apply, norm); break;
                case "XOR": filter = new ArithmeticFilter(ImageOperations.XOR::apply, norm); break;
            }
            applyFilter(filter, true);
        };

        tabActions[0] = updateAction;

        cbImgA.addActionListener(e -> updateAction.run());
        cbImgB.addActionListener(e -> updateAction.run());
        cbFilter.addActionListener(e -> updateAction.run());
        chkNormalize.addActionListener(e -> updateAction.run());

        panel.add(new JLabel("Imagem A:"));
        panel.add(cbImgA);
        panel.add(new JLabel("Imagem B:"));
        panel.add(cbImgB);
        panel.add(new JLabel("Operação:"));
        panel.add(cbFilter);
        panel.add(chkNormalize);

        return panel;
    }

    private JPanel createEnhancementTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        JComboBox<String> cbImgA = new JComboBox<>(imgNamesDefault);
        String[] filters = {"Passa-Altas", "Aguçamento", "Prewitt", "Sobel", "Roberts", "Alto Reforço"};
        JComboBox<String> cbFilter = new JComboBox<>(filters);

        Runnable updateAction = () -> {
            loadImg(imgA, canvasA, cbImgA.getSelectedIndex());
            clearCanvas(canvasB);
            
            ImageFilter filter = null;
            switch ((String) cbFilter.getSelectedItem()) {
                case "Passa-Altas": filter = new HighPassFilter(); break;
                case "Aguçamento": filter = new SharpenFilter(); break;
                case "Prewitt": filter = new PrewittFilter(); break;
                case "Sobel": filter = new SobelFilter(); break;
                case "Roberts": filter = new RobertsFilter(); break;
                case "Alto Reforço": filter = new HighBoostFilter(); break;
            }
            applyFilter(filter, false);
        };

        tabActions[1] = updateAction;

        cbImgA.addActionListener(e -> updateAction.run());
        cbFilter.addActionListener(e -> updateAction.run());

        panel.add(new JLabel("Imagem Base:"));
        panel.add(cbImgA);
        panel.add(new JLabel("Filtro Espacial:"));
        panel.add(cbFilter);

        return panel;
    }

    private JPanel createSmoothingTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        String[] filters = {"Média (Requer Gaussiano)", "Mediana (Requer Sal/Pimenta)"};
        JComboBox<String> cbFilter = new JComboBox<>(filters);

        Runnable updateAction = () -> {
            clearCanvas(canvasB);
            ImageFilter filter = null;
            
            if (cbFilter.getSelectedIndex() == 0) {
                loadImg(imgA, canvasA, 2); 
                filter = new MeanFilter();
            } else {
                loadImg(imgA, canvasA, 3); 
                filter = new MedianFilter();
            }
            applyFilter(filter, false);
        };

        tabActions[2] = updateAction;

        cbFilter.addActionListener(e -> updateAction.run());

        panel.add(new JLabel("Técnica de remoção de ruído:"));
        panel.add(cbFilter);

        return panel;
    }

    private JPanel createIntensityTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        JComboBox<String> cbImgA = new JComboBox<>(imgNamesDefault);
        String[] filters = {"Linear", "Logarítmica", "Sigmoide", "Equalização de Histograma"};
        JComboBox<String> cbFilter = new JComboBox<>(filters);

        Runnable updateAction = () -> {
            loadImg(imgA, canvasA, cbImgA.getSelectedIndex());
            clearCanvas(canvasB);
            
            ImageFilter filter = null;
            switch ((String) cbFilter.getSelectedItem()) {
                case "Linear": filter = new LinearTransformFilter(1.2, 20); break;
                case "Logarítmica": filter = new LogarithmicFilter(); break;
                case "Sigmoide": filter = new SigmoidFilter(127.0, 25.0); break;
                case "Equalização de Histograma": filter = new HistogramEqualizationFilter(); break;
            }
            applyFilter(filter, false);
        };

        tabActions[3] = updateAction;

        cbImgA.addActionListener(e -> updateAction.run());
        cbFilter.addActionListener(e -> updateAction.run());

        panel.add(new JLabel("Imagem Base:"));
        panel.add(cbImgA);
        panel.add(new JLabel("Transformação:"));
        panel.add(cbFilter);

        return panel;
    }

    private JPanel createMorphingTab() {
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

        tabActions[4] = updateAction;

        timeSlider.addChangeListener(e -> updateAction.run());

        panel.add(new JLabel("Transição de Tempo (t):"));
        panel.add(timeSlider);

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

    private void saveProcessedImage() {
        if (processedImg.w == 0 || processedImg.data == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma imagem processada para download.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("output.pgm"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            ImageUtils.download(chooser.getSelectedFile().getAbsolutePath(), processedImg);
            JOptionPane.showMessageDialog(this, "Imagem salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}