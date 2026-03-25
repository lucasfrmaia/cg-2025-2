package com.cg.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ImageConverter {

    public static void convertToPGM(String inputPath, String outputPath, int targetWidth, int targetHeight) {
        try {
            File inputFile = new File(inputPath);
            if (!inputFile.exists()) {
                System.out.println("Arquivo de origem nao encontrado: " + inputPath);
                return;
            }

            BufferedImage original = ImageIO.read(inputFile);
            if (original == null) {
                System.out.println("Nao foi possivel ler a imagem de origem.");
                return;
            }

            BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
            g2d.dispose();

            try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
                writer.println("P2");
                writer.println(targetWidth + " " + targetHeight);
                writer.println("255");

                int count = 0;
                for (int y = 0; y < targetHeight; y++) {
                    for (int x = 0; x < targetWidth; x++) {
                        int rgb = resized.getRGB(x, y);
                        int gray = rgb & 0xFF;
                        writer.print(gray + " ");
                        count++;
                        if (count % 15 == 0) {
                            writer.println();
                        }
                    }
                }
            }
            System.out.println("Convertido e salvo: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String baseDir = "/home/joaoaguiar/Documentos/CG/cg-2025-2/Projeto-2/cg/imagens/";
        
        String inputCrianca = baseDir + "minha_foto_crianca.jpg";
        String inputAtual = baseDir + "minha_foto_atual.jpg";
        
        String outputCrianca = baseDir + "crianca.pgm";
        String outputAtual = baseDir + "atual.pgm";

        convertToPGM(inputCrianca, outputCrianca, 256, 256);
        convertToPGM(inputAtual, outputAtual, 256, 256);
    }
}