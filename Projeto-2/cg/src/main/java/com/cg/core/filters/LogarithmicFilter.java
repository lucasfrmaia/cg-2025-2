package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class LogarithmicFilter implements ImageFilter {

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        double c = 255.0 / Math.log(256.0);

        for (int i = 0; i < img.data.length; i++) {
            int val = (int) Math.round(c * Math.log(img.data[i] + 1));
            result.data[i] = Math.max(0, Math.min(255, val));
        }

        return result;
    }
}