package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class NegativeFilter implements ImageFilter {

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        for (int i = 0; i < img.data.length; i++) {
            result.data[i] = 255 - img.data[i];
        }

        return result;
    }
}