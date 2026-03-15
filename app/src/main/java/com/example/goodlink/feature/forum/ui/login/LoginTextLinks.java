package com.example.goodlink.feature.forum.ui.login;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LoginTextLinks {
    private LoginTextLinks(){}

    public static SpannableString buildRegisterLink(String text, int color, Runnable onRegisterClick) {
        SpannableString s = new SpannableString(text);
        applyFirstMatch(s, "registre-se", color, onRegisterClick);
        return s;
    }

    private static void applyFirstMatch(SpannableString spannable, String regex, int color, Runnable onClick) {
        if (onClick == null) return;

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(spannable.toString());
        if (!matcher.find()) return;

        int start = matcher.start();
        int end = matcher.end();

        spannable.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override public void onClick(@NonNull View widget) { onClick.run(); }
            @Override public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(color);
                ds.setUnderlineText(false);
            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
