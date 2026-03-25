package com.cg.utils;

import com.cg.core.ImageOperations;
import com.cg.model.ImageModel;
import com.cg.model.PGMImage;

import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ImageUtils {

    public static ImageModel readPGM(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String magicNumber = readNextValidToken(reader);
            
            if (!"P2".equals(magicNumber)) {
                throw new IllegalArgumentException("Suporta apenas formato P2 (ASCII)");
            }

            int width = Integer.parseInt(readNextValidToken(reader));
            int height = Integer.parseInt(readNextValidToken(reader));
            Integer.parseInt(readNextValidToken(reader));

            ImageModel image = new ImageModel(width, height);
            int[][] pixels = image.getPixels();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[y][x] = Integer.parseInt(readNextValidToken(reader));
                }
            }

            return image;
        }
    }

    private static String readNextValidToken(BufferedReader reader) throws IOException {
        StringBuilder token = new StringBuilder();
        int c;
        boolean inComment = false;

        while ((c = reader.read()) != -1) {
            char ch = (char) c;

            if (inComment) {
                if (ch == '\n' || ch == '\r') {
                    inComment = false;
                }
                continue;
            }

            if (ch == '#') {
                inComment = true;
                continue;
            }

            if (Character.isWhitespace(ch)) {
                if (token.length() > 0) {
                    return token.toString();
                }
            } else {
                token.append(ch);
            }
        }

        return token.length() > 0 ? token.toString() : null;
    }

    public static void writePGM(ImageModel image, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("P2\n");
            writer.write(image.getWidth() + " " + image.getHeight() + "\n");
            writer.write("255\n");

            int[][] pixels = image.getPixels();
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    writer.write(pixels[y][x] + " ");
                }
                writer.write("\n");
            }
        }
    }

    public static void readImage(String filePath, PGMImage pgmImage) {
        try {
            ImageModel model = readPGM(filePath);
            pgmImage.type = "P2";
            pgmImage.w = model.getWidth();
            pgmImage.h = model.getHeight();
            pgmImage.data = new int[model.getWidth() * model.getHeight()];
            
            int[][] pixels = model.getPixels();
            int idx = 0;
            for (int y = 0; y < model.getHeight(); y++) {
                for (int x = 0; x < model.getWidth(); x++) {
                    pgmImage.data[idx++] = pixels[y][x];
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao ler arquivo PGM:\n" + filePath + "\n" + e.getMessage(),
                    "Arquivo inválido",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null,
                    "Formato inválido ou arquivo corrompido:\n" + filePath + "\n" + e.getMessage(),
                    "Arquivo inválido",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static int[] composition(int[] dataA, int[] dataB, int width, int height, 
                                    ImageOperations op, boolean normalize) {
        int[] result = new int[dataA.length];

        for (int i = 0; i < dataA.length; i++) {
            result[i] = op.apply(dataA[i], dataB[i]);
        }

        if (normalize) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (int val : result) {
                min = Math.min(min, val);
                max = Math.max(max, val);
            }

            if (min == max) {
                int gray = Math.min(255, Math.max(0, result[0]));
                for (int i = 0; i < result.length; i++) {
                    result[i] = gray;
                }
            } else {
                for (int i = 0; i < result.length; i++) {
                    int scaled = (result[i] - min) * 255 / (max - min);
                    result[i] = Math.min(255, Math.max(0, scaled));
                }
            }
        } else {
            for (int i = 0; i < result.length; i++) {
                result[i] = Math.min(255, Math.max(0, result[i]));
            }
        }

        return result;
    }

    public static void download(String filePath, PGMImage pgmImage) {
        try {
            ImageModel model = new ImageModel(pgmImage.w, pgmImage.h);
            int[][] pixels = model.getPixels();
            
            int idx = 0;
            for (int y = 0; y < pgmImage.h; y++) {
                for (int x = 0; x < pgmImage.w; x++) {
                    pixels[y][x] = pgmImage.data[idx++];
                }
            }
            
            writePGM(model, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}