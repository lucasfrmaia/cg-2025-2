package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class RobertsFilter implements ImageFilter {

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        for (int y = 0; y < img.h - 1; y++) {
            for (int x = 0; x < img.w - 1; x++) {
                int z5 = img.data[y * img.w + x];
                int z6 = img.data[y * img.w + (x + 1)];
                int z8 = img.data[(y + 1) * img.w + x];
                int z9 = img.data[(y + 1) * img.w + (x + 1)];

                int val = Math.abs(z5 - z9) + Math.abs(z6 - z8);
                result.data[y * img.w + x] = Math.max(0, Math.min(255, val));
            }
        }

        return result;
    }
}