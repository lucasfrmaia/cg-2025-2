package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.core.ImageProcessor;
import com.cg.model.PGMImage;

public class MeanFilter implements ImageFilter {
    
    @Override
    public PGMImage apply(PGMImage img, PGMImage... additionalSources) {
        double[][] mask = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
        };
        
        return ImageProcessor.applyConvolution(img, mask, 1.0 / 9.0);
    }
}