package com.example.goodlink.Screens;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goodlink.FCM.FCMMessagingService;
import com.example.goodlink.FireBaseManager.FireBaseAuthenticate;
import com.example.goodlink.FireBaseManager.FireBaseDataBase;
import com.example.goodlink.FireBaseManager.ManagerSession;
import com.example.goodlink.R;
import com.example.goodlink.Utils.FontSizeUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private FireBaseAuthenticate mAuthenticator;
    private ManagerSession managerSession;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView connectEmail, btnRegisterPage, forgotPass;
    private CheckBox checkBoxLog;
    private Button btnLogin;
    private ImageButton btnGoogle;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initUI();
        setupListener();
        setupFirebase();
    }

    private void initUI() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        connectEmail = findViewById(R.id.connectEmailPageLogin);
        checkBoxLog = findViewById(R.id.checkBoxLog);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterPage = findViewById(R.id.btnRegisterPage);
        forgotPass = findViewById(R.id.forgotePass);
        btnGoogle = findViewById(R.id.btnGoogle);

        FontSizeUtils.applySpecificFontSize(connectEmail, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(btnLogin, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(editTextEmail, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(editTextPassword, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(btnRegisterPage, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(checkBoxLog, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(forgotPass, FontSizeUtils.getFontSize(this));

        btnRegisterPage.setText(getSpannableStringText(btnRegisterPage.getText().toString(), btnRegisterPage));
        btnRegisterPage.setMovementMethod(LinkMovementMethod.getInstance());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forumScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupFirebase() {
        FireBaseDataBase database = new FireBaseDataBase();
        mAuthenticator = new FireBaseAuthenticate(database, this);
        managerSession = new ManagerSession(this);
    }

    private void setupListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String senha = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
                    showToast("Por favor, preencha todos os campos");
                    return;
                }

                mAuthenticator.loginUser(email, senha, new AuthenticationListener() {
                    @Override
                    public void onLoginSuccess(FirebaseUser user) {
                        goToActivity(Forum.class);
                    }

                    @Override
                    public void onLoginFailure(String errorMessage) {
                        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

                if (checkBoxLog.isChecked()) {
                    managerSession.setLogin(true);
                }
            }
        });

        btnGoogle.setOnClickListener(v -> signInWithGoogle(this, RC_SIGN_IN));

        forgotPass.setOnClickListener(v -> startActivity(new Intent(Login.this, ResetPass.class)));
    }

    private void signInWithGoogle(Activity activity, int requestCode) {
        if (mAuthenticator.getGoogleSignInClient() != null) {
            Intent signInIntent = mAuthenticator.getGoogleSignInClient().getSignInIntent();
            startActivityForResult(signInIntent, requestCode);
        } else {
            Log.e(TAG, "Erro ao configurar Google Sign-In.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    mAuthenticator.signUpWithGoogle(idToken, new FireBaseAuthenticate.GoogleSignInCallback() {
                        @Override
                        public void onSuccess(FirebaseUser user) {
                            retrieveFCMToken(new Register.FCMTokenCallBack() {
                                @Override
                                public void onSuccess(String token) {
                                    goToActivity(Forum.class);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    showToast("Reinicie o aplicativo");
                                    Log.e(TAG, "Erro ao obter o token FCM: ", e);
                                }
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e(TAG, "Erro ao autenticar com Google: " + errorMessage);
                        }
                    });
                }
            } catch (ApiException e) {
                Log.e(TAG, "Erro na autenticação com Google: " + e.getStatusCode());
                showToast("Erro ao se registrar com Google Sign-In.");
            }
        }
    }

    private void retrieveFCMToken(Register.FCMTokenCallBack callBack) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                new FCMMessagingService().saveTokenToPrefs(task.getResult());
                callBack.onSuccess(task.getResult());
            } else {
                callBack.onFailure(task.getException());
                Log.e(TAG, "Erro ao obter o token FCM: ", task.getException());
            }
        });
    }

    private SpannableString getSpannableStringText(String checkBoxText, TextView textView) {
        SpannableString spannableString = new SpannableString(checkBoxText);

        if (textView.getId() == R.id.btnRegisterPage) {
            applyClickableAndColoredSpan(spannableString, "Registre-se", 0xFF0099DD, this::openPageRegister);
        }

        return spannableString;
    }

    private void applyClickableAndColoredSpan(SpannableString spannable, String regex, int color, Runnable onClick) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(spannable.toString());

        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            spannable.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    onClick.run();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(color);
                    ds.setUnderlineText(false);
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void openPageRegister() {
        goToActivity(Register.class);
    }

    private void goToActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }

    public interface AuthenticationListener {
        void onLoginSuccess(FirebaseUser user);
        void onLoginFailure(String errorMessage);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}