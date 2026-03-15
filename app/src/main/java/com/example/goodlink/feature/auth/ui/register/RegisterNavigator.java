package com.example.goodlink.feature.auth.ui.register;

import android.app.Activity;
import android.content.Intent;

import com.example.goodlink.feature.forum.ui.login.LoginActivity;
import com.example.goodlink.feature.legal.ui.PolicyActivity;
import com.example.goodlink.feature.legal.ui.TermsAcitivity;

public final class RegisterNavigator {
    private RegisterNavigator(){}

    public static void toLogin(Activity a) {
        goAndFinish(a, LoginActivity.class);
    }

    public static void toTerms(Activity a) {
        go(a, TermsAcitivity.class);
    }

    public static void toPolicy(Activity a) {
        go(a, PolicyActivity.class);
    }

    private static void go(Activity a, Class<?> c) {
        a.startActivity(new Intent(a, c));
    }

    private static void goAndFinish(Activity a, Class<?> c) {
        a.startActivity(new Intent(a, c));
        a.finish();
    }
}
