package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;
import java.util.Arrays;

public class MedianFilter implements ImageFilter {

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        int[] window = new int[9];

        for (int y = 1; y < img.h - 1; y++) {
            for (int x = 1; x < img.w - 1; x++) {
                int k = 0;
                for (int my = -1; my <= 1; my++) {
                    for (int mx = -1; mx <= 1; mx++) {
                        window[k++] = img.data[(y + my) * img.w + (x + mx)];
                    }
                }
                Arrays.sort(window);
                result.data[y * img.w + x] = window[4];
            }
        }

        return result;
    }
}