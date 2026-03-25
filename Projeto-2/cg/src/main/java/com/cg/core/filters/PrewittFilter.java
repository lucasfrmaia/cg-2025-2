package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class PrewittFilter implements ImageFilter {

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        int[][] gx = {{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}};
        int[][] gy = {{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}};

        for (int y = 1; y < img.h - 1; y++) {
            for (int x = 1; x < img.w - 1; x++) {
                int sumX = 0;
                int sumY = 0;

                for (int my = -1; my <= 1; my++) {
                    for (int mx = -1; mx <= 1; mx++) {
                        int pixel = img.data[(y + my) * img.w + (x + mx)];
                        sumX += pixel * gx[my + 1][mx + 1];
                        sumY += pixel * gy[my + 1][mx + 1];
                    }
                }

                int val = Math.abs(sumX) + Math.abs(sumY);
                result.data[y * img.w + x] = Math.max(0, Math.min(255, val));
            }
        }

        return result;
    }
}