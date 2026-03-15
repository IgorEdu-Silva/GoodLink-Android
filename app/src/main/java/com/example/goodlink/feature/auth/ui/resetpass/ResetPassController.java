package com.example.goodlink.feature.auth.ui.resetpass;

import android.util.Log;

import com.example.goodlink.infrastructure.firebase.auth.FireBaseAuthenticate;

public final class ResetPassController {

    public interface Ui {
        void showToast(String message);
        void onNavigateBackToLogin();
    }

    private final FireBaseAuthenticate authenticator;
    private final Ui ui;

    public ResetPassController(FireBaseAuthenticate authenticator, Ui ui) {
        this.authenticator = authenticator;
        this.ui = ui;
    }

    public void submit(String email) {
        ResetPassValidator.Result res = ResetPassValidator.validateEmail(email);
        if (!res.ok) {
            ui.showToast(res.message);
            return;
        }

        authenticator.resetPassword(email, new FireBaseAuthenticate.ResetPasswordListener() {
            @Override
            public void onResetSuccess() {
                ui.showToast("Email de redefinição de senha enviado com sucesso.");
                ui.onNavigateBackToLogin();
            }

            @Override
            public void onResetFailure(String errorMessage) {
                Log.e("ResetPassController", errorMessage);

                // mínimo: traduzir essa mensagem específica
                if (errorMessage != null && errorMessage.contains("There is no user record corresponding to this identifier")) {
                    ui.showToast("O email não está cadastrado.");
                } else {
                    ui.showToast("Erro ao redefinir a senha: " + errorMessage);
                }
            }
        });
    }
}
