package com.example.goodlink.Screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goodlink.FireBaseManager.FireBaseAuthenticate;
import com.example.goodlink.FireBaseManager.FireBaseDataBase;
import com.example.goodlink.FireBaseManager.ManagerSession;
import com.example.goodlink.R;
import com.example.goodlink.Utils.FontSizeUtils;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FireBaseAuthenticate mAuthenticator;
    private FireBaseDataBase mDatabase;
    private CheckBox checkBoxServices;
    private ManagerSession managerSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forumScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FireBaseDataBase database = new FireBaseDataBase();
        mAuthenticator = new FireBaseAuthenticate(database);
        managerSession = new ManagerSession(this);

        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        TextView connectEmail = findViewById(R.id.connectEmailPageLogin);
        CheckBox checkBoxLog = findViewById(R.id.checkBoxLog);

        if (managerSession.isLoggedIn()) {
            goToActivity();
        }

        TextView buttonRegisterPager = findViewById(R.id.btnRegisterPage);
        buttonRegisterPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });


        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String senha = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
                    Toast.makeText(Login.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean manterLogado = checkBoxLog.isChecked();
                mAuthenticator.loginUser(email, senha, new AuthenticationListener() {
                    @Override
                    public void onLoginSuccess(FirebaseUser user) {
                        Intent intent = new Intent(Login.this, Forum.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onLoginFailure(String errorMessage) {
                        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                if (manterLogado) {
                    managerSession.setLogin(true);
                }
            }
        });

        TextView ForgotPass = findViewById(R.id.forgotePass);
        ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ResetPass.class);
                startActivity(intent);
            }
        });

        FontSizeUtils.applySpecificFontSize(connectEmail, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(btnLogin, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(editTextEmail, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(editTextPassword, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(buttonRegisterPager, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(checkBoxLog, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(ForgotPass, FontSizeUtils.getFontSize(this));

    }

    private void goToActivity() {
        Intent intent = new Intent(this, Forum.class);
        startActivity(intent);
        finish();
    }

    public Context getContext() {
        return null;
    }

    public interface AuthenticationListener {
        void onLoginSuccess(FirebaseUser user);
        void onLoginFailure(String errorMessage);
    }

}