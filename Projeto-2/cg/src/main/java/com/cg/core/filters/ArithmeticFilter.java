package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.core.ImageOperations;
import com.cg.model.ImageModel;

public class ArithmeticFilter implements ImageFilter {
    private final ImageOperations.Operator operator;
    private final boolean normalize;

    public ArithmeticFilter(ImageOperations.Operator operator, boolean normalize) {
        this.operator = operator;
        this.normalize = normalize;
    }

    @Override
    public ImageModel apply(ImageModel source, ImageModel... additionalSources) {
        ImageModel target = additionalSources[0];
        ImageModel result = new ImageModel(source.getWidth(), source.getHeight());
        
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int val = operator.apply(source.getPixels()[y][x], target.getPixels()[y][x]);
                result.getPixels()[y][x] = normalize ? Math.min(255, Math.max(0, val)) : val;
            }
        }
        return result;
    }
}