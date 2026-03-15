package com.example.goodlink.feature.auth.ui.resetpass;

import android.text.TextUtils;

public final class ResetPassValidator {
    private ResetPassValidator(){}

    public static Result validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return Result.error("Por favor, insira seu e-mail.");
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
