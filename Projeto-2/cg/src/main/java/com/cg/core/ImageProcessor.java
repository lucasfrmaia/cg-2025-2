package com.cg.core;

import com.cg.model.ImageModel;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessor {
    private final List<ImageFilter> pipeline = new ArrayList<>();

    public void addFilter(ImageFilter filter) {
        pipeline.add(filter);
    }

    public void clearPipeline() {
        pipeline.clear();
    }

    public ImageModel process(ImageModel initialImage, ImageModel... additionalImages) {
        ImageModel currentImage = initialImage;
        for (ImageFilter filter : pipeline) {
            currentImage = filter.apply(currentImage, additionalImages);
        }
        return currentImage;
    }
}