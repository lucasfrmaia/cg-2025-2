package com.cg.ui;

import com.cg.model.PGMImage;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Dimension;

public class ImageCanvas extends JPanel {
    private BufferedImage image;

    public ImageCanvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(100, 100));
    }

    public void setImage(PGMImage pgmImage) {
        if (pgmImage.w > 0 && pgmImage.h > 0 && pgmImage.data != null) {
            BufferedImage buf = new BufferedImage(pgmImage.w, pgmImage.h, BufferedImage.TYPE_BYTE_GRAY);
            int idx = 0;
            for (int y = 0; y < pgmImage.h; y++) {
                for (int x = 0; x < pgmImage.w; x++) {
                    int gray = pgmImage.data[idx++];
                    if (gray < 0) gray = 0;
                    if (gray > 255) gray = 255;
                    int rgb = (gray << 16) | (gray << 8) | gray;
                    buf.setRGB(x, y, rgb);
                }
            }
            image = buf;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int panelW = getWidth();
            int panelH = getHeight();
            int imageW = image.getWidth();
            int imageH = image.getHeight();

            double scale = Math.min(1.0, Math.min((double) panelW / imageW, (double) panelH / imageH));
            int drawW = (int) Math.round(imageW * scale);
            int drawH = (int) Math.round(imageH * scale);

            int x = (panelW - drawW) / 2;
            int y = (panelH - drawH) / 2;

            g.drawImage(image, x, y, drawW, drawH, null);
        }
    }
}