package com.example.goodlink.feature.auth.ui.register;

import android.app.Activity;
import android.util.Log;

import com.example.goodlink.infrastructure.fcm.FcmTokenProvider;
import com.example.goodlink.infrastructure.firebase.auth.FireBaseAuthenticate;

public final class RegisterController {

    public interface Ui {
        void showToast(String message);
        void onNavigateToLogin();
    }

    private final FireBaseAuthenticate authenticator;
    private final FcmTokenProvider tokenProvider;
    private final Ui ui;

    public RegisterController(FireBaseAuthenticate authenticator, FcmTokenProvider tokenProvider, Ui ui) {
        this.authenticator = authenticator;
        this.tokenProvider = tokenProvider;
        this.ui = ui;
    }

    public void register(Activity activity, String nome, String email, String senha, boolean aceitouTermos) {
        RegisterValidator.ValidationResult result =
                RegisterValidator.validate(nome, email, senha, aceitouTermos);

        if (!result.ok) {
            ui.showToast(result.message);
            return;
        }

        authenticator.registerUser(nome, email, senha, activity, new FireBaseAuthenticate.RegistrationCallback() {
            @Override
            public void onRegistrationSuccess() {
                tokenProvider.fetchAndPersist(new FcmTokenProvider.Callback() {
                    @Override
                    public void onSuccess(String token) {
                        ui.onNavigateToLogin();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ui.showToast("Reinicie o aplicativo");
                        Log.e("RegisterController", "Erro ao obter token FCM", e);
                    }
                });
            }

            @Override
            public void onRegistrationFailure(String errorMessage) {
                Log.e("RegisterController", "Falha ao registrar: " + errorMessage);
                ui.showToast(errorMessage);
            }
        });
    }
}
