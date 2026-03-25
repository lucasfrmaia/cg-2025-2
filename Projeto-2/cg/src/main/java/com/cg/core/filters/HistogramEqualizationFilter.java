package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class HistogramEqualizationFilter implements ImageFilter {

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        int[] hist = new int[256];
        for (int i = 0; i < img.data.length; i++) {
            hist[img.data[i]]++;
        }

        int[] cdf = new int[256];
        cdf[0] = hist[0];
        for (int i = 1; i < 256; i++) {
            cdf[i] = cdf[i - 1] + hist[i];
        }

        int totalPixels = img.w * img.h;
        int[] map = new int[256];
        for (int i = 0; i < 256; i++) {
            map[i] = Math.max(0, Math.min(255, Math.round(255.0f * cdf[i] / totalPixels)));
        }

        for (int i = 0; i < img.data.length; i++) {
            result.data[i] = map[img.data[i]];
        }

        return result;
    }
}