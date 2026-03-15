package com.example.goodlink.feature.forum.ui.login;

import android.app.Activity;
import android.util.Log;

import com.example.goodlink.infrastructure.fcm.FcmTokenProvider;
import com.example.goodlink.infrastructure.firebase.auth.FireBaseAuthenticate;
import com.example.goodlink.infrastructure.session.LoginStateStorage;
import com.google.firebase.auth.FirebaseUser;

public final class LoginController {

    public interface Ui {
        void showToast(String message);
        void onNavigateToForum();
    }

    private final FireBaseAuthenticate authenticator;
    private final LoginStateStorage loginStateStorage;
    private final FcmTokenProvider tokenProvider;
    private final Ui ui;

    public LoginController(
            FireBaseAuthenticate authenticator,
            LoginStateStorage loginStateStorage,
            FcmTokenProvider tokenProvider,
            Ui ui
    ) {
        this.authenticator = authenticator;
        this.loginStateStorage = loginStateStorage;
        this.tokenProvider = tokenProvider;
        this.ui = ui;
    }

    public void loginWithEmail(String email, String senha, boolean keepLogged) {
        LoginValidator.Result result = LoginValidator.validate(email, senha);
        if (!result.ok) {
            ui.showToast(result.message);
            return;
        }

        authenticator.loginUser(email, senha, new FireBaseAuthenticate.AuthenticationListener() {
            @Override public void onLoginSuccess(FirebaseUser user) {
                if (keepLogged) loginStateStorage.setLogin(true);
                ui.onNavigateToForum();
            }

            @Override public void onLoginFailure(String errorMessage) {
                ui.showToast(errorMessage);
                Log.e("LoginController", errorMessage);
            }
        });
    }

    public void loginWithGoogleToken(String idToken) {
        if (idToken == null || idToken.trim().isEmpty()) {
            ui.showToast("Erro ao autenticar com Google");
            return;
        }

        authenticator.signInWithGoogle(idToken, new FireBaseAuthenticate.GoogleSignInCallback() {
            @Override public void onSuccess(FirebaseUser user) {
                loginStateStorage.setLogin(true);

                tokenProvider.fetchAndPersist(new FcmTokenProvider.Callback() {
                    @Override public void onSuccess(String token) { ui.onNavigateToForum(); }
                    @Override public void onFailure(Exception e) {
                        ui.showToast("Reinicie o aplicativo");
                        Log.e("LoginController", "Erro ao obter token FCM", e);
                    }
                });
            }

            @Override public void onFailure(String errorMessage) {
                ui.showToast(errorMessage);
                Log.e("LoginController", errorMessage);
            }
        });
    }

    public void autoRedirectIfLogged() {
        if (loginStateStorage.isLoggedIn()) {
            ui.onNavigateToForum();
        }
    }
}
