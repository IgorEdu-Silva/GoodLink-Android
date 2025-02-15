package com.example.goodlink.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.goodlink.R;

public class FontSizeUtils {
    private static final String PREFS_NAME = "FontSizePrefs";
    private static final String FONT_SIZE_KEY = "font_size";
    private static final String TAG = "FontSizeUtils";

    public static void saveFontSize(Context context, float fontSize) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(FONT_SIZE_KEY, fontSize).apply();
    }

    public static float getFontSize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(FONT_SIZE_KEY, 16);
    }

    public static Context applyFontSize(Context baseContext) {
        float fontSize = getFontSize(baseContext);

        return new ContextThemeWrapper(baseContext, R.style.Theme_GoodLink) {
            @Override
            public Object getSystemService(String name) {
                if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
                    LayoutInflater inflater = (LayoutInflater) super.getSystemService(name);
                    inflater.setFactory2(new LayoutInflater.Factory2() {
                        @Override
                        public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                            View view = null;

                            try {
                                if (name.equals("TextView")) {
                                    view = inflater.createView(name, "android.widget.", attrs);
                                } else if (name.equals("EditText")) {
                                    view = inflater.createView(name, "android.widget.", attrs);
                                } else {
                                    view = inflater.createView(name, null, attrs);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Erro ao criar a view: " + name, e);
                            }

                            if (view instanceof TextView) {
                                applyTextSize((TextView) view, fontSize);
                            } else if (view instanceof EditText) {
                                applyTextSize((EditText) view, fontSize);
                            }

                            return view;
                        }

                        @Override
                        public View onCreateView(View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                            return null;
                        }
                    });
                    return inflater;
                }
                return super.getSystemService(name);
            }
        };
    }

    private static void applyTextSize(TextView textView, float fontSize) {
        textView.setTextSize(fontSize);
    }

    private static void applyTextSize(EditText editText, float fontSize) {
        editText.setTextSize(fontSize);
    }

    public static void applySpecificFontSize(View view, float fontSize) {
        if (view instanceof TextView) {
            applyTextSize((TextView) view, fontSize);
        } else if (view instanceof EditText) {
            applyTextSize((EditText) view, fontSize);
        } else {
            Log.d(TAG, "Não há aplicação de tamanho de fonte para esta View: " + view.getClass().getSimpleName());
        }
    }
}
