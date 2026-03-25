package com.cg.core;

import com.cg.model.ImageModel;

public interface ImageFilter {
    ImageModel apply(ImageModel source, ImageModel... additionalSources);
}