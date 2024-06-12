package com.example.goodlink.Screens;

import static android.content.ContentValues.TAG;

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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goodlink.FireBase.FireBaseAuthenticate;
import com.example.goodlink.FireBase.FireBaseDataBase;
import com.example.goodlink.FireBase.MessagingService;
import com.example.goodlink.FireBase.SessionManager;
import com.example.goodlink.Functions.NotificationHelper;
import com.example.goodlink.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Register extends AppCompatActivity {
    private FireBaseAuthenticate mAuthenticator;
    private FireBaseDataBase mDatabase;
    private CheckBox checkBoxServices;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FireBaseDataBase database = new FireBaseDataBase();
        mAuthenticator = new FireBaseAuthenticate(database);
        sessionManager = new SessionManager(this);

        NotificationHelper.requestNotificationPermission(this);

        if (sessionManager.isLoggedIn()) {
            goToActivity();
        }

        Button buttonLoginPager = findViewById(R.id.buttonLoginPager);
        buttonLoginPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
            }
        });

        Button buttonRegisterPager = findViewById(R.id.buttonRegisterPager);
        buttonRegisterPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Register.this.getClass().equals(Register.class)) {
                    Intent intent = new Intent(Register.this, Register.class);
                    startActivity(intent);
                }
            }
        });

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextName = findViewById(R.id.editTextName);
                EditText editTextEmail = findViewById(R.id.editTextEmail);
                EditText editTextPassword = findViewById(R.id.editTextPassword);

                String nome = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String senha = editTextPassword.getText().toString();

                CheckBox checkBoxServices = findViewById(R.id.checkBoxServices);
                boolean aceitouTermos = checkBoxServices.isChecked();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(Register.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else if (senha.length() < 8) {
                    Toast.makeText(Register.this, "A senha deve ter no mínimo " + 8 + " caracteres", Toast.LENGTH_SHORT).show();
                } else if (!aceitouTermos) {
                    Toast.makeText(Register.this, "Por favor, aceite os termos e condições", Toast.LENGTH_SHORT).show();
                } else {
                    mAuthenticator.registerUser(nome, email, senha, Register.this);

                    FireBaseAuthenticate.RegistrationCallback registrationCallback = new FireBaseAuthenticate.RegistrationCallback() {
                        @Override
                        public void onRegistrationSuccess() {
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        String token = task.getResult();
                                        MessagingService messagingService = new MessagingService();
                                        messagingService.saveTokenToPrefs(token);
                                    } else {
                                        Log.e(TAG, "Erro ao obter o token FCM: " + task.getException());
                                    }
                                }
                            });

                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onRegistrationFailure(String errorMessage) {
                        }
                    };

                }
            }
        });

        CheckBox checkBoxServices = findViewById(R.id.checkBoxServices);
        String checkBoxText = checkBoxServices.getText().toString();
        SpannableString spannableStringTermos = getSpannableStringTermos(checkBoxText);
        checkBoxServices.setText(spannableStringTermos);
        checkBoxServices.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @NonNull
    private SpannableString getSpannableStringTermos(String checkBoxText) {
        int startIndex = checkBoxText.indexOf("termos e politica");
        int endIndex = startIndex + "termos e politica".length();

        SpannableString spannableString = new SpannableString(checkBoxText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xFF0099DD);
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                openPageTermos();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public void openPageTermos() {
        Intent intent = new Intent(this, Termos.class);
        startActivity(intent);
    }

    private void goToActivity() {
        Intent intent = new Intent(this, Forum.class);
        startActivity(intent);
        finish();
    }
}