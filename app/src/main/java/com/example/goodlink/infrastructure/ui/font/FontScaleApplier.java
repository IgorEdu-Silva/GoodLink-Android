// src/main/java/com/example/goodlink/infrastructure/ui/FontScaleApplier.java
package com.example.goodlink.infrastructure.ui.font;

import android.content.Context;
import android.content.res.Configuration;

public final class FontScaleApplier {
    private FontScaleApplier() {}

    public static Context wrap(Context base, float fontSizeSp) {
        if (fontSizeSp <= 0f) fontSizeSp = 14f;
        float scale = fontSizeSp / 14f;
        Configuration cfg = new Configuration(base.getResources().getConfiguration());
        cfg.fontScale = scale;
        return base.createConfigurationContext(cfg);
    }
}