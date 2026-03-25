package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.core.ImageOperations;
import com.cg.model.PGMImage;

public class ArithmeticFilter implements ImageFilter {
    private final ImageOperations.Operator operator;
    private final boolean normalize;

    public ArithmeticFilter(ImageOperations.Operator operator, boolean normalize) {
        this.operator = operator;
        this.normalize = normalize;
    }

    @Override
    public PGMImage apply(PGMImage source, PGMImage... additionalSources) {
        if (additionalSources == null || additionalSources.length == 0 || additionalSources[0].data == null) {
            return source;
        }

        PGMImage target = additionalSources[0];
        PGMImage result = new PGMImage();
        result.w = source.w;
        result.h = source.h;
        result.type = source.type;
        result.data = new int[source.w * source.h];

        int[] temp = new int[source.data.length];
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < source.data.length; i++) {
            int val = operator.apply(source.data[i], target.data[i]);
            temp[i] = val;
            if (val < min) min = val;
            if (val > max) max = val;
        }

        for (int i = 0; i < temp.length; i++) {
            if (normalize) {
                if (max == min) {
                    result.data[i] = Math.max(0, Math.min(255, temp[i]));
                } else {
                    result.data[i] = (int) Math.round(255.0 * (temp[i] - min) / (max - min));
                }
            } else {
                result.data[i] = Math.max(0, Math.min(255, temp[i]));
            }
        }
        
        return result;
    }
}