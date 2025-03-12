package com.example.goodlink.Screens;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        initUI();
        setupListener();
        setupInsets();
        setupFirebase();
    }

    private void initUI() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxServices = findViewById(R.id.checkBoxServices);
        btnRegister = findViewById(R.id.btnRegister);
        buttonLoginPager = findViewById(R.id.btnLoginPage);

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
                        logErrorException("Method registerUser on Register.class", "Erro ao obter o token FCM: ", e);
                    }
                });
            }

            @Override
            public void onRegistrationFailure(String errorMessage) {
                logError("Method registerUser on Register.class", errorMessage);
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

    private void retrieveFCMToken(FCMTokenCallBack callBack) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                new FCMMessagingService().saveTokenToPrefs(getApplicationContext(), task.getResult());
                callBack.onSuccess(task.getResult());
            } else {
                callBack.onFailure(task.getException());
                logErrorException("Method retrieveFCMToken", "Erro ao obter token FCM: " , task.getException());
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

    private void logError(String tag, String error){
        Log.e(tag, error);
    }

    private void logErrorException(String tag, String error, Exception e){
        Log.e(tag, error, e);
    }
}