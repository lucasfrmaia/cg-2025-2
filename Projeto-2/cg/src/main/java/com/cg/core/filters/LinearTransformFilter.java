package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class LinearTransformFilter implements ImageFilter {
    private final double a;
    private final double b;

    public LinearTransformFilter(double a, double b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        for (int i = 0; i < img.data.length; i++) {
            int val = (int) Math.round(a * img.data[i] + b);
            result.data[i] = Math.max(0, Math.min(255, val));
        }

        return result;
    }
}