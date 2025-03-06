package com.example.goodlink.Screens;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
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

import com.example.goodlink.FireBaseManager.FireBaseAuthenticate;
import com.example.goodlink.FireBaseManager.FireBaseDataBase;
import com.example.goodlink.FCM.FCMMessagingService;
import com.example.goodlink.FireBaseManager.ManagerSession;
import com.example.goodlink.Functions.HelperNotification;
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

public class Register extends AppCompatActivity {

    private FireBaseAuthenticate mAuthenticator;
    private ManagerSession managerSession;
    private EditText editTextName, editTextEmail, editTextPassword;
    private CheckBox checkBoxServices;
    private Button btnRegister;
    private TextView buttonLoginPager;
    private ImageButton btnGoogle, btnGitHub;
    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private static final int RC_SIGN_IN_GITHUB = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        initUI();
        setupListener();
        setupInsets();
        setupFirebase();

        if (managerSession.isLoggedIn()) {
            goToActivity(Forum.class);
        }
    }

    private void initUI() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxServices = findViewById(R.id.checkBoxServices);
        btnRegister = findViewById(R.id.btnRegister);
        buttonLoginPager = findViewById(R.id.btnLoginPage);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnGitHub = findViewById(R.id.btnGitHub);

        FontSizeUtils.applySpecificFontSize(editTextEmail, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(editTextName, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(editTextPassword, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(buttonLoginPager, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(btnRegister, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(checkBoxServices, FontSizeUtils.getFontSize(this));

        SpannableString spannableStringTermos = getSpannableStringText(checkBoxServices.getText().toString(), checkBoxServices);
        checkBoxServices.setText(spannableStringTermos);
        checkBoxServices.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString spannableStringLogin = getSpannableStringText(buttonLoginPager.getText().toString(), buttonLoginPager);
        buttonLoginPager.setText(spannableStringLogin);
        buttonLoginPager.setMovementMethod(LinkMovementMethod.getInstance());

        buttonLoginPager.setText(getSpannableStringText(buttonLoginPager.getText().toString(), buttonLoginPager));
        buttonLoginPager.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupListener() {
        btnRegister.setOnClickListener(v -> registerUser());
        btnGoogle.setOnClickListener(v -> signInWithGoogle(this, RC_SIGN_IN_GOOGLE));
        btnGitHub.setOnClickListener(v -> signInWithGitHub(this, RC_SIGN_IN_GITHUB));
    }

    private void setupInsets() {
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
        HelperNotification.requestNotificationPermission(this);
    }

    private void registerUser() {
        String nome = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String senha = editTextPassword.getText().toString().trim();
        boolean aceitouTermos = checkBoxServices.isChecked();

        if (!validateInputs(nome, email, senha, aceitouTermos)) return;

        mAuthenticator.registerUser(nome, email, senha, Register.this, new FireBaseAuthenticate.RegistrationCallback() {
            @Override
            public void onRegistrationSuccess() {
                retrieveFCMToken(new FCMTokenCallBack() {
                    @Override
                    public void onSuccess(String token) {
                        goToActivity(Login.class);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        showToast("Reinicie o aplicativo");
                        Log.e(TAG, "Erro ao obter o token FCM: ", e);
                    }
                });
            }

            @Override
            public void onRegistrationFailure(String errorMessage) {
                Log.e(TAG, "Erro ao registrar: " + errorMessage);
            }
        });
    }

    private boolean validateInputs(String nome, String email, String senha, boolean aceitouTermos) {
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            showToast("Por favor, preencha todos os campos");
            return false;
        }
        if (senha.length() < 12) {
            showToast("A senha deve ter no mínimo 12 caracteres");
            return false;
        }
        if (!aceitouTermos) {
            showToast("Por favor, aceite os termos e condições");
            return false;
        }
        return true;
    }

    private void signInWithGoogle(Activity activity, int requestCode) {
        if (mAuthenticator.getGoogleSignInClient() != null) {
            Intent signInIntent = mAuthenticator.getGoogleSignInClient().getSignInIntent();
            startActivityForResult(signInIntent, requestCode);
        } else {
            Log.e(TAG, "Erro ao configurar Google Sign-In.");
        }
    }

    public void signInWithGitHub(Activity activity, int requestCode) {
        String githubUrl = "https://github.com/login/oauth/authorize?client_id=v23lixrIADTRCGhhQLc&scope=user:email";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    mAuthenticator.signInWithGoogle(idToken, new FireBaseAuthenticate.GoogleSignInCallback() {
                        @Override
                        public void onSuccess(FirebaseUser user) {
                            retrieveFCMToken(new FCMTokenCallBack() {
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

        if (requestCode == RC_SIGN_IN_GITHUB) {
            Uri githubUri = data.getData();
            if (githubUri != null) {
                String idToken = githubUri.getQueryParameter("idToken");
                if (idToken != null) {
                    mAuthenticator.signInWithGitHub(idToken, new FireBaseAuthenticate.GitHubSignInCallback() {
                        @Override
                        public void onSuccess(FirebaseUser user) {
                            retrieveFCMToken(new FCMTokenCallBack() {
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
                            Log.e(TAG, "Erro ao autenticar com GitHub: " + errorMessage);
                        }
                    });
                }
            }
        }
    }

    private void retrieveFCMToken(FCMTokenCallBack callBack) {
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

        applyClickableAndColoredSpan(spannableString, "termos", 0xFF0099DD,this::openPageTermos);
        applyClickableAndColoredSpan(spannableString, "politica", 0xFF0099DD, this::openPagePolicy);

        if (textView.getId() == R.id.btnLoginPage) {
            applyClickableAndColoredSpan(spannableString, "Entrar", 0xFF0099DD, this::openPageLogin);
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

    private void openPageLogin() {
        goToActivity(Login.class);
    }

    private void openPageTermos() {
        goToActivity(Termos.class);
    }

    private void openPagePolicy() {
        goToActivity(Policy.class);
    }

    private void goToActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }

    public interface FCMTokenCallBack {
        void onSuccess(String token);
        void onFailure(Exception e);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}