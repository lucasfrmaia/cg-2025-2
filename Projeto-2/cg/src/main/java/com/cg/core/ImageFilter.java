package com.cg.core;

import com.cg.model.PGMImage;

public interface ImageFilter {
    PGMImage apply(PGMImage source, PGMImage... additionalSources);
}