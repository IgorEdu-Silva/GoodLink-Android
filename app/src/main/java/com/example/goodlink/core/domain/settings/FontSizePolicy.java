package com.example.goodlink.core.domain.settings;

public class FontSizePolicy {
    private FontSizePolicy() {}

    public static float clamp(float sizeSp) {
        if (sizeSp < FontSizeLimits.MIN_SP) return FontSizeLimits.MIN_SP;
        if (sizeSp > FontSizeLimits.MAX_SP) return FontSizeLimits.MAX_SP;
        return sizeSp;
    }
}
