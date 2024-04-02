package com.example.goodlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResetPass extends AppCompatActivity implements FireBaseAuthenticate.ResetPasswordListener{
    private FireBaseAuthenticate mAuthenticator;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FireBaseDataBase database = new FireBaseDataBase();
        mAuthenticator = new FireBaseAuthenticate(database);
        sessionManager = new SessionManager(this);

        EditText emailForReset = findViewById(R.id.EmailForReset);
        Button confirmReset = findViewById(R.id.ConfirmReset);

        confirmReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailForReset.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ResetPass.this, "Por favor, insira seu e-mail.", Toast.LENGTH_SHORT).show();
                } else {
                    mAuthenticator.resetPassword(email, (ResetPasswordListener) ResetPass.this);
                }
            }
        });


        Button BackToLogin = findViewById(R.id.BackToLogin);

        BackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPass.this, LoginAndRegister.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResetSuccess() {

    }

    @Override
    public void onResetFailure(String errorMessage) {

    }

    public interface ResetPasswordListener {
        void onResetSuccess();
        void onResetFailure(String errorMessage);
    }
}