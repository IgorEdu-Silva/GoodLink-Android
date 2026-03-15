package com.example.goodlink.feature.auth.ui.register;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegisterTextLinks {
    private RegisterTextLinks(){}

    /**
     * @param text         Texto original do TextView/CheckBox
     * @param color        Cor do link (ARGB), ex: 0xFF0099DD
     * @param onTerms      Ação ao clicar em "termos"
     * @param onPolicy     Ação ao clicar em "política"
     * @param onLogin      Ação ao clicar em "Entrar" (opcional)
     * @param includeLogin Se true, tenta criar link para "Entrar"
     */
    public static SpannableString build(
            String text,
            int color,
            Runnable onTerms,
            Runnable onPolicy,
            Runnable onLogin,
            boolean includeLogin
    ) {
        SpannableString s = new SpannableString(text);

        // Ajuste os regex conforme o texto real no layout (acentos etc).
        applyFirstMatch(s, "termos", color, onTerms);
        applyFirstMatch(s, "politica|política", color, onPolicy);

        if (includeLogin) {
            applyFirstMatch(s, "entrar", color, onLogin);
        }

        return s;
    }

    private static void applyFirstMatch(SpannableString spannable, String regex, int color, Runnable onClick) {
        if (onClick == null) return;

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(spannable.toString());

        if (!matcher.find()) return;

        int start = matcher.start();
        int end = matcher.end();

        spannable.setSpan(
                new ForegroundColorSpan(color),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        onClick.run();
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.setColor(color);
                        ds.setUnderlineText(false);
                    }
                },
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }
}
