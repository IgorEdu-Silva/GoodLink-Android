package com.example.goodlink.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goodlink.FireBaseManager.FireBaseAuthenticate;
import com.example.goodlink.FireBaseManager.FireBaseDataBase;
import com.example.goodlink.R;

public class ResetPass extends AppCompatActivity {
    private EditText emailForReset;
    private FireBaseAuthenticate mAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        mAuthenticator = new FireBaseAuthenticate(new FireBaseDataBase());

        emailForReset = findViewById(R.id.EmailForReset);
        Button confirmReset = findViewById(R.id.ConfirmReset);
        Button backToLogin = findViewById(R.id.BackToLogin);

        confirmReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailForReset.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ResetPass.this, "Por favor, insira seu e-mail.", Toast.LENGTH_SHORT).show();
                } else {
                    resetPassword(email);
                }
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPass.this, Login.class));
                finish();
            }
        });
    }

    private void resetPassword(String email) {
        mAuthenticator.resetPassword(email, new FireBaseAuthenticate.ResetPasswordListener() {
            @Override
            public void onResetSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ResetPass.this, "Email de redefinição de senha enviado com sucesso.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResetFailure(String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (errorMessage.contains("There is no user record corresponding to this identifier")) {
                            Toast.makeText(ResetPass.this, "O email não está cadastrado.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPass.this, "Erro ao redefinir a senha: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}