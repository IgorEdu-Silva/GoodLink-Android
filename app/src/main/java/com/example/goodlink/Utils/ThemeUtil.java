package com.example.goodlink.Utils;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.widget.TextView;

public class ThemeUtil {
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;

    private static int currentTheme = THEME_LIGHT;

    public static int getCurrentTheme() {
        return currentTheme;
    }

    public static void setTheme(int theme) {
        currentTheme = theme;
    }

    public static void applyThemeToTextView(TextView textView, Context context) {
        int textColor;

        if (currentTheme == THEME_DARK) {
            textColor = context.getResources().getColor(android.R.color.white);
        } else {
            textColor = context.getResources().getColor(android.R.color.black);
        }

        if (textView != null) {
            textView.setTextColor(textColor);
        }
    }

    public static void applyThemeToBackground(View view, Context context) {
        int backgroundColor;

        if (currentTheme == THEME_DARK) {
            backgroundColor = context.getResources().getColor(android.R.color.black);
        } else {
            backgroundColor = context.getResources().getColor(android.R.color.white);
        }

        if (view != null) {
            view.setBackgroundColor(backgroundColor);
        }
    }

    public static void syncWithSystemTheme(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(THEME_DARK);
        } else {
            setTheme(THEME_LIGHT);
        }
    }
}
