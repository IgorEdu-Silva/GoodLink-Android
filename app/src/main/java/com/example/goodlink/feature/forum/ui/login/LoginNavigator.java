package com.example.goodlink.feature.forum.ui.login;

import android.app.Activity;
import android.content.Intent;

import com.example.goodlink.feature.auth.ui.resetpass.ResetPassActivity;
import com.example.goodlink.feature.auth.ui.register.RegisterActivity;
import com.example.goodlink.feature.forum.ui.ForumActivity;

public final class LoginNavigator {
    private LoginNavigator(){}

    public static void toForum(Activity a) { goFinish(a, ForumActivity.class); }
    public static void toRegister(Activity a) { goFinish(a, RegisterActivity.class); }
    public static void toResetPass(Activity a) { a.startActivity(new Intent(a, ResetPassActivity.class)); }

    private static void goFinish(Activity a, Class<?> c) {
        a.startActivity(new Intent(a, c));
        a.finish();
    }
}
