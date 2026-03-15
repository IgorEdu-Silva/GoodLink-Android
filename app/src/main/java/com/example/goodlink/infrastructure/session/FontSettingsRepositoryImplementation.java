package com.example.goodlink.infrastructure.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.goodlink.core.domain.settings.FontSettingsGateway;

public final class FontSettingsRepositoryImplementation implements FontSettingsGateway {

    private static final String PREFS_NAME = "font_prefs";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final float DEFAULT_SIZE = 14f;

    private final SharedPreferences prefs;

    public FontSettingsRepositoryImplementation(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public float getFontSize() {
        return prefs.getFloat(KEY_FONT_SIZE, DEFAULT_SIZE);
    }

    @Override
    public void setFontSize(float size) {
        prefs.edit().putFloat(KEY_FONT_SIZE, size).apply();
    }
}