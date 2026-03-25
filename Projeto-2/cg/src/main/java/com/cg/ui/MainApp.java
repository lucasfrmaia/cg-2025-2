package com.cg.ui;

import com.cg.core.ImageOperations;
import com.cg.model.PGMImage;
import com.cg.utils.ImageUtils;
import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private final PGMImage imgA = new PGMImage();
    private final PGMImage imgB = new PGMImage();
    private final PGMImage processedImg = new PGMImage();

    private final ImageCanvas canvasA;
    private final ImageCanvas canvasB;
    private final ImageCanvas canvasProcessed;

    private boolean doNormalize = true;

    private final String[] imagePaths = {
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/lena.pgm",
        "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/Airplane.pgm",
    };

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

        JPanel controlsPanel = new JPanel(new FlowLayout());

        String[] imageNames = {"Lena", "Airplane"};
        JComboBox<String> imgASelector = new JComboBox<>(imageNames);
        JComboBox<String> imgBSelector = new JComboBox<>(imageNames);
        JComboBox<ImageOperations> filterSelector = new JComboBox<>(ImageOperations.values());
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

        add(canvasesPanel, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.SOUTH);

        imgASelector.addActionListener(e -> {
            int idx = imgASelector.getSelectedIndex();
            ImageUtils.readImage(imagePaths[idx], imgA);
            canvasA.setImage(imgA);
            applyFilter((ImageOperations) filterSelector.getSelectedItem());
        });

        imgBSelector.addActionListener(e -> {
            int idx = imgBSelector.getSelectedIndex();
            ImageUtils.readImage(imagePaths[idx], imgB);
            canvasB.setImage(imgB);
            applyFilter((ImageOperations) filterSelector.getSelectedItem());
        });

        filterSelector.addActionListener(e -> applyFilter((ImageOperations) filterSelector.getSelectedItem()));
        
        normalizeSwitch.addActionListener(e -> {
            doNormalize = normalizeSwitch.isSelected();
            applyFilter((ImageOperations) filterSelector.getSelectedItem());
        });
        
        downloadBtn.addActionListener(e -> {
            if (processedImg.w == 0 || processedImg.data == null) {
                JOptionPane.showMessageDialog(this, "Nenhuma imagem processada para download.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar imagem PGM");
            chooser.setSelectedFile(new java.io.File("output.pgm"));
            int userSelection = chooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = chooser.getSelectedFile();
                ImageUtils.download(fileToSave.getAbsolutePath(), processedImg);
                JOptionPane.showMessageDialog(this, "Imagem salva em: " + fileToSave.getAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        imgASelector.setSelectedIndex(0);
        imgBSelector.setSelectedIndex(1);

        applyFilter((ImageOperations) filterSelector.getSelectedItem());

        pack();
        setLocationRelativeTo(null);
    }

    private void applyFilter(ImageOperations op) {
        if (op != null && imgA.data != null && imgB.data != null) {
            processedImg.type = imgA.type;
            processedImg.w = imgA.w;
            processedImg.h = imgA.h;
            processedImg.data = ImageUtils.composition(imgA.data, imgB.data, imgA.w, imgA.h, op, doNormalize);
            canvasProcessed.setImage(processedImg);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}