package com.cg.core;

import com.cg.model.PGMImage;

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

    public PGMImage process(PGMImage initialImage, PGMImage... additionalImages) {
        PGMImage currentImage = initialImage;
        for (ImageFilter filter : pipeline) {
            currentImage = filter.apply(currentImage, additionalImages);
        }
        return currentImage;
    }

    public static PGMImage applyConvolution(PGMImage img, double[][] mask, double factor) {
        PGMImage result = new PGMImage();
        result.w = img.w;
        result.h = img.h;
        result.type = img.type;
        result.data = new int[img.w * img.h];

        int offset = mask.length / 2;

        for (int y = offset; y < img.h - offset; y++) {
            for (int x = offset; x < img.w - offset; x++) {
                double sum = 0.0;

                for (int my = 0; my < mask.length; my++) {
                    for (int mx = 0; mx < mask[0].length; mx++) {
                        int py = Math.max(0, Math.min(img.h - 1, y + my - offset));
                        int px = Math.max(0, Math.min(img.w - 1, x + mx - offset));
                        sum += img.data[py * img.w + px] * mask[my][mx];
                    }
                }

                int val = (int) Math.round(sum * factor);
                val = Math.max(0, Math.min(255, val));
                result.data[y * img.w + x] = val;
            }
        }

        return result;
    }
}