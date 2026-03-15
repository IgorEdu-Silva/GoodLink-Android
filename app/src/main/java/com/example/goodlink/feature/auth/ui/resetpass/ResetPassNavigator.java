package com.example.goodlink.feature.auth.ui.resetpass;

import android.app.Activity;
import android.content.Intent;

import com.example.goodlink.feature.forum.ui.login.LoginActivity;

public final class ResetPassNavigator {
    private ResetPassNavigator(){}

    public static void toLogin(Activity a) {
        a.startActivity(new Intent(a, LoginActivity.class));
        a.finish();
    }
}
