package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class SigmoidFilter implements ImageFilter {
    private final double w;
    private final double sigma;

    public SigmoidFilter(double w, double sigma) {
        this.w = w;
        this.sigma = sigma;
    }

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        for (int i = 0; i < img.data.length; i++) {
            double val = 255.0 / (1.0 + Math.exp(-(img.data[i] - w) / sigma));
            result.data[i] = Math.max(0, Math.min(255, (int) Math.round(val)));
        }

        return result;
    }
}