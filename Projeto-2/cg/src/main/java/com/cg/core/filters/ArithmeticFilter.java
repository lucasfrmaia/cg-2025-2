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
        if (additionalSources == null || additionalSources.length == 0 || additionalSources[0] == null) {
            return source; 
        }

        PGMImage target = additionalSources[0];
        
        PGMImage result = new PGMImage();
        result.w = source.w;
        result.h = source.h;
        result.type = source.type;
        result.data = new int[source.w * source.h];
        
        for (int i = 0; i < source.data.length; i++) {
            int val = operator.apply(source.data[i], target.data[i]);
            result.data[i] = normalize ? Math.min(255, Math.max(0, val)) : val;
        }
        
        return result;
    }
}