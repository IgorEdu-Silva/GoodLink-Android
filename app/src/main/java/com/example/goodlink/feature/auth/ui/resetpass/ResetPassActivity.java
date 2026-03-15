package com.example.goodlink.feature.auth.ui.resetpass;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goodlink.R;
import com.example.goodlink.infrastructure.firebase.auth.FireBaseAuthenticate;
import com.example.goodlink.infrastructure.firebase.database.FireBaseDataBase;

public class ResetPassActivity extends AppCompatActivity implements ResetPassController.Ui {

    private EditText emailForReset;
    private ResetPassController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        emailForReset = findViewById(R.id.EmailForReset);
        Button confirmReset = findViewById(R.id.ConfirmReset);
        ImageButton backToLogin = findViewById(R.id.BackToLogin);

        // services
        FireBaseDataBase database = new FireBaseDataBase();
        FireBaseAuthenticate authenticator = new FireBaseAuthenticate(database, this);
        controller = new ResetPassController(authenticator, this);

        // listeners
        confirmReset.setOnClickListener(v ->
                controller.submit(emailForReset.getText().toString().trim())
        );

        backToLogin.setOnClickListener(v -> ResetPassNavigator.toLogin(this));
    }

    @Override
    public void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onNavigateBackToLogin() {
        ResetPassNavigator.toLogin(this);
    }
}
