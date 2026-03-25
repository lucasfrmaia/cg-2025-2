package com.cg.core.filters;

import com.cg.core.ImageFilter;
import com.cg.model.ImageModel;

public class SobelFilter implements ImageFilter {
    @Override
    public ImageModel apply(ImageModel source, ImageModel... additionalSources) {
        ImageModel result = new ImageModel(source.getWidth(), source.getHeight());
        
        return result;
    }
}