package com.example.goodlink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseUser;

public class LoginAndRegister extends AppCompatActivity {
    private FireBaseAuthenticate mAuthenticator;
    private FireBaseDataBase mDatabase;
    private CheckBox checkBoxServices;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_and_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FireBaseDataBase database = new FireBaseDataBase();
        mAuthenticator = new FireBaseAuthenticate(database);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            goToActivity();
        }

        Button buttonRegisterPager = findViewById(R.id.buttonRegisterPager);
        buttonRegisterPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAndRegister.this, RegisterAndLogin.class);
                startActivity(intent);
            }
        });

        Button buttonLoginPager = findViewById(R.id.buttonLoginPager);
        buttonLoginPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginAndRegister.this.getClass().equals(LoginAndRegister.class)) {
                    Intent intent = new Intent(LoginAndRegister.this, LoginAndRegister.class);
                    startActivity(intent);
                }
            }
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextEmail = findViewById(R.id.editTextEmail);
                EditText editTextPassword = findViewById(R.id.editTextPassword);

                String email = editTextEmail.getText().toString();
                String senha = editTextPassword.getText().toString();

                CheckBox checkBoxLog = findViewById(R.id.checkBoxLog);
                boolean manterLogado = checkBoxLog.isChecked();
                mAuthenticator.loginUser(email, senha, new AuthenticationListener() {
                    @Override
                    public void onLoginSuccess(FirebaseUser user) {
                        Intent intent = new Intent(LoginAndRegister.this, forum.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onLoginFailure(String errorMessage) {
                        Toast.makeText(LoginAndRegister.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                if (manterLogado) {
                    sessionManager.setLogin(true);
                }
            }
        });

        TextView textViewForgotPass = findViewById(R.id.forgotePass);
        textViewForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAndRegister.this, ResetPass.class);
                startActivity(intent);
            }
        });
    }

    private void goToActivity() {
        Intent intent = new Intent(this, forum.class);
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