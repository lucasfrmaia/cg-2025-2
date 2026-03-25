package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.PGMImage;

public class DynamicRangeFilter implements ImageFilter {

    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        int min = 255;
        int max = 0;

        for (int val : img.data) {
            if (val < min) min = val;
            if (val > max) max = val;
        }

        if (max == min) {
            return img; 
        }

        for (int i = 0; i < img.data.length; i++) {
            int val = (int) Math.round(255.0 * (img.data[i] - min) / (max - min));
            result.data[i] = Math.max(0, Math.min(255, val));
        }

        return result;
    }
}