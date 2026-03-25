package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class GammaFilter implements ImageFilter {
    private final double gamma;

    public GammaFilter(double gamma) {
        this.gamma = gamma;
    }

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        for (int i = 0; i < img.data.length; i++) {
            double r = img.data[i] / 255.0; 
            double s = Math.pow(r, gamma);
            result.data[i] = Math.max(0, Math.min(255, (int) Math.round(s * 255.0)));
        }

        return result;
    }
}