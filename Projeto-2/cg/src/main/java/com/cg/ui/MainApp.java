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

    private boolean doNormalize = true;

    private final String[] imagePaths = {
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/lena.pgm",
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/Airplane.pgm",
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/Lenag.pgm",
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/Lenasalp.pgm"
    };

    private final String[] imagesMode1 = {"Lena", "Airplane"};
    private final String[] imagesMode2 = {"Lena (Gaussiano)", "Lena (Sal e Pimenta)"};
    
    private final String[] filtersMode1 = {
        "Soma", "Subtração", "Multiplicação", "Divisão", 
        "OR", "AND", "XOR", 
        "Passa-Altas", "Aguçamento", "Prewitt", "Sobel", "Roberts", "Alto Reforço"
    };
    private final String[] filtersMode2 = {"Média", "Mediana"};
    private final String[] filtersMode3 = {"Linear", "Logarítmica", "Sigmoide", "Equalização de Histograma"};

    private JComboBox<String> imgASelector;
    private JComboBox<String> imgBSelector;
    private JComboBox<String> filterSelector;
    private JComboBox<String> modeSelector;

    public MainApp() {
        setTitle("Processador de Imagens");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel canvasesPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        canvasA = new ImageCanvas(256, 256);
        canvasB = new ImageCanvas(256, 256);
        canvasProcessed = new ImageCanvas(256, 256);

        canvasesPanel.add(canvasA);
        canvasesPanel.add(canvasB);
        canvasesPanel.add(canvasProcessed);

        JPanel topPanel = new JPanel(new FlowLayout());
        modeSelector = new JComboBox<>(new String[]{"Operações e Realce", "Suavização de Ruído", "Transformações de Intensidade"});
        topPanel.add(new JLabel("Modo de Processamento:"));
        topPanel.add(modeSelector);

        JPanel controlsPanel = new JPanel(new FlowLayout());
        imgASelector = new JComboBox<>(imagesMode1);
        imgBSelector = new JComboBox<>(imagesMode1);
        filterSelector = new JComboBox<>(filtersMode1);
        
        JCheckBox normalizeSwitch = new JCheckBox("Normalizar", true);
        JButton downloadBtn = new JButton("Download");

        controlsPanel.add(new JLabel("Img A:"));
        controlsPanel.add(imgASelector);
        controlsPanel.add(new JLabel("Img B:"));
        controlsPanel.add(imgBSelector);
        controlsPanel.add(new JLabel("Filtro:"));
        controlsPanel.add(filterSelector);
        controlsPanel.add(normalizeSwitch);
        controlsPanel.add(downloadBtn);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(topPanel);
        bottomPanel.add(controlsPanel);

        add(canvasesPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        modeSelector.addActionListener(e -> {
            int mode = modeSelector.getSelectedIndex();
            
            if (mode == 0) {
                imgASelector.setModel(new DefaultComboBoxModel<>(imagesMode1));
                imgBSelector.setModel(new DefaultComboBoxModel<>(imagesMode1));
                filterSelector.setModel(new DefaultComboBoxModel<>(filtersMode1));
                imgBSelector.setEnabled(true);
                loadImage(0, imgA, canvasA, 0);
                loadImage(1, imgB, canvasB, 0);
            } else if (mode == 1) {
                imgASelector.setModel(new DefaultComboBoxModel<>(imagesMode2));
                imgBSelector.setModel(new DefaultComboBoxModel<>(imagesMode2));
                filterSelector.setModel(new DefaultComboBoxModel<>(filtersMode2));
                imgBSelector.setEnabled(false);
                canvasB.setImage(new PGMImage());
                canvasB.repaint();
                loadImage(0, imgA, canvasA, 1);
            } else {
                imgASelector.setModel(new DefaultComboBoxModel<>(imagesMode1));
                imgBSelector.setModel(new DefaultComboBoxModel<>(imagesMode1));
                filterSelector.setModel(new DefaultComboBoxModel<>(filtersMode3));
                imgBSelector.setEnabled(false);
                canvasB.setImage(new PGMImage());
                canvasB.repaint();
                loadImage(0, imgA, canvasA, 2);
            }
            
            applyFilter((String) filterSelector.getSelectedItem());
        });

        imgASelector.addActionListener(e -> {
            loadImage(imgASelector.getSelectedIndex(), imgA, canvasA, modeSelector.getSelectedIndex());
            applyFilter((String) filterSelector.getSelectedItem());
        });

        imgBSelector.addActionListener(e -> {
            loadImage(imgBSelector.getSelectedIndex(), imgB, canvasB, modeSelector.getSelectedIndex());
            applyFilter((String) filterSelector.getSelectedItem());
        });

        filterSelector.addActionListener(e -> {
            String selectedFilter = (String) filterSelector.getSelectedItem();
            if (selectedFilter == null) return;
            
            if (modeSelector.getSelectedIndex() == 1) {
                if (selectedFilter.equals("Média")) {
                    imgASelector.setSelectedIndex(0);
                } else if (selectedFilter.equals("Mediana")) {
                    imgASelector.setSelectedIndex(1);
                }
            }
            applyFilter(selectedFilter);
        });
        
        normalizeSwitch.addActionListener(e -> {
            doNormalize = normalizeSwitch.isSelected();
            applyFilter((String) filterSelector.getSelectedItem());
        });
        
        downloadBtn.addActionListener(e -> {
            if (processedImg.w == 0 || processedImg.data == null) {
                JOptionPane.showMessageDialog(this, "Nenhuma imagem processada para download.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar imagem PGM");
            chooser.setSelectedFile(new File("output.pgm"));
            int userSelection = chooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                ImageUtils.download(fileToSave.getAbsolutePath(), processedImg);
                JOptionPane.showMessageDialog(this, "Imagem salva em: " + fileToSave.getAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadImage(0, imgA, canvasA, 0);
        loadImage(1, imgB, canvasB, 0);
        applyFilter((String) filterSelector.getSelectedItem());

        pack();
        setLocationRelativeTo(null);
    }

    private void loadImage(int selectorIndex, PGMImage targetImg, ImageCanvas targetCanvas, int mode) {
        if (selectorIndex < 0) return;
        int pathIndex = (mode == 1) ? selectorIndex + 2 : selectorIndex;
        ImageUtils.readImage(imagePaths[pathIndex], targetImg);
        targetCanvas.setImage(targetImg);
        targetCanvas.repaint();
    }

    private void applyFilter(String filterName) {
        if (imgA.data == null || filterName == null) return;

        ImageFilter filter = null;
        boolean requiresTwoImages = false;

        switch (filterName) {
            case "Soma": filter = new ArithmeticFilter(ImageOperations.ADD::apply, doNormalize); requiresTwoImages = true; break;
            case "Subtração": filter = new ArithmeticFilter(ImageOperations.SUB::apply, doNormalize); requiresTwoImages = true; break;
            case "Multiplicação": filter = new ArithmeticFilter(ImageOperations.MUL::apply, doNormalize); requiresTwoImages = true; break;
            case "Divisão": filter = new ArithmeticFilter(ImageOperations.DIVIDE::apply, doNormalize); requiresTwoImages = true; break;
            case "OR": filter = new ArithmeticFilter(ImageOperations.OR::apply, doNormalize); requiresTwoImages = true; break;
            case "AND": filter = new ArithmeticFilter(ImageOperations.AND::apply, doNormalize); requiresTwoImages = true; break;
            case "XOR": filter = new ArithmeticFilter(ImageOperations.XOR::apply, doNormalize); requiresTwoImages = true; break;
            case "Passa-Altas": filter = new HighPassFilter(); break;
            case "Aguçamento": filter = new SharpenFilter(); break;
            case "Prewitt": filter = new PrewittFilter(); break;
            case "Sobel": filter = new SobelFilter(); break;
            case "Roberts": filter = new RobertsFilter(); break;
            case "Alto Reforço": filter = new HighBoostFilter(); break;
            case "Média": filter = new MeanFilter(); break;
            case "Mediana": filter = new MedianFilter(); break;
            case "Linear": filter = new LinearTransformFilter(1.2, 20); break;
            case "Logarítmica": filter = new LogarithmicFilter(); break;
            case "Sigmoide": filter = new SigmoidFilter(127.0, 25.0); break;
            case "Equalização de Histograma": filter = new HistogramEqualizationFilter(); break;
        }

        if (filter != null) {
            if (requiresTwoImages && imgB.data != null) {
                processedImg = filter.apply(imgA, imgB);
            } else if (!requiresTwoImages) {
                processedImg = filter.apply(imgA);
            }
            
            if (processedImg != null && processedImg.data != null) {
                canvasProcessed.setImage(processedImg);
                canvasProcessed.repaint();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}