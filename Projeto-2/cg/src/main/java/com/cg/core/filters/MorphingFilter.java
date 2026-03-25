package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class MorphingFilter implements ImageFilter {
    private final double t;

    public MorphingFilter(double t) {
        this.t = Math.max(0.0, Math.min(1.0, t));
    }

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        if (additionalSources == null || additionalSources.length == 0 || additionalSources[0].data == null) {
            return img;
        }

        PGMImage target = additionalSources[0];
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        for (int i = 0; i < img.data.length; i++) {
            double val = (1.0 - t) * img.data[i] + t * target.data[i];
            result.data[i] = Math.max(0, Math.min(255, (int) Math.round(val)));
        }

        return result;
    }
}