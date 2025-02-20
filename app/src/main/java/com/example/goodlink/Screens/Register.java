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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.goodlink.FireBaseManager.FireBaseAuthenticate;
import com.example.goodlink.FireBaseManager.FireBaseDataBase;
import com.example.goodlink.FCM.FCMMessagingService;
import com.example.goodlink.FireBaseManager.ManagerSession;
import com.example.goodlink.Fragments.FragmentPageContainerIntroduction;
import com.example.goodlink.Functions.HelperNotification;
import com.example.goodlink.R;
import com.example.goodlink.Utils.FontSizeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Register extends AppCompatActivity {
    private FireBaseAuthenticate mAuthenticator;
    private FireBaseDataBase mDatabase;
    private CheckBox checkBoxServices;
    private ManagerSession managerSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forumScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FireBaseDataBase database = new FireBaseDataBase();
        mAuthenticator = new FireBaseAuthenticate(database);
        managerSession = new ManagerSession(this);

        HelperNotification.requestNotificationPermission(this);

        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);

        if (managerSession.isLoggedIn()) {
            goToActivity();
        } else {
            goToFragment();
        }

        TextView buttonLoginPager = findViewById(R.id.btnLoginPage);
        buttonLoginPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
            }
        });


        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String senha = editTextPassword.getText().toString();

                CheckBox checkBoxServices = findViewById(R.id.checkBoxServices);
                boolean aceitouTermos = checkBoxServices.isChecked();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(Register.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else if (senha.length() < 12) {
                    Toast.makeText(Register.this, "A senha deve ter no mínimo " + 12 + " caracteres", Toast.LENGTH_SHORT).show();
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
                                        FCMMessagingService FCMMessagingService = new FCMMessagingService();
                                        FCMMessagingService.saveTokenToPrefs(token);
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

        FontSizeUtils.applySpecificFontSize(editTextEmail, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(editTextName, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(editTextPassword, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(buttonLoginPager, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(btnRegister, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(checkBoxServices, FontSizeUtils.getFontSize(this));
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

    private void goToFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.ViewPagerIntroduction) == null){
            Fragment fragment = new FragmentPageContainerIntroduction();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayoutScreenRegister, fragment)
                    .commit();
        }
    }
}
