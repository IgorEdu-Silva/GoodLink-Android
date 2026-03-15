package com.example.goodlink.feature.forum.ui.login;

import android.text.TextUtils;

public final class LoginValidator {
    private LoginValidator(){}

    public static Result validate(String email, String senha) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            return Result.error("Por favor, preencha todos os campos");
        }
        return Result.ok();
    }

    public static final class Result {
        public final boolean ok;
        public final String message;

        private Result(boolean ok, String message) { this.ok = ok; this.message = message; }
        public static Result ok() { return new Result(true, null); }
        public static Result error(String msg) { return new Result(false, msg); }
    }
}
