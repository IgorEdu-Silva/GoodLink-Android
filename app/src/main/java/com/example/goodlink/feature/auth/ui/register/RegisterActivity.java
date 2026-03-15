package com.example.goodlink.feature.auth.ui.register;

import android.os.Bundle;
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

import com.example.goodlink.R;
import com.example.goodlink.infrastructure.fcm.FcmTokenProvider;
import com.example.goodlink.infrastructure.firebase.auth.FireBaseAuthenticate;
import com.example.goodlink.infrastructure.firebase.database.FireBaseDataBase;
import com.example.goodlink.infrastructure.fcm.FCMMessagingService;
import com.example.goodlink.infrastructure.notification.HelperNotification;

public class RegisterActivity extends AppCompatActivity implements RegisterController.Ui {

    private EditText editTextName, editTextEmail, editTextPassword;
    private CheckBox checkBoxServices;
    private Button btnRegister;
    private TextView buttonLoginPager;

    private RegisterController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        initUI();
        setupInsets();
        setupLinks();
        setupServices();
        setupListener();
    }

    private void initUI() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxServices = findViewById(R.id.checkBoxServices);
        btnRegister = findViewById(R.id.btnRegister);
        buttonLoginPager = findViewById(R.id.btnLoginPage);
    }

    private void setupServices() {
        FireBaseDataBase database = new FireBaseDataBase();
        FireBaseAuthenticate authenticator = new FireBaseAuthenticate(database, this);

        HelperNotification.requestNotificationPermission(this);

        FcmTokenProvider tokenProvider =
                new FcmTokenProvider(getApplicationContext(), new FCMMessagingService());

        controller = new RegisterController(authenticator, tokenProvider, this);
    }

    private void setupListener() {
        btnRegister.setOnClickListener(v -> controller.register(
                this,
                editTextName.getText().toString().trim(),
                editTextEmail.getText().toString().trim(),
                editTextPassword.getText().toString().trim(),
                checkBoxServices.isChecked()
        ));
    }

    private void setupLinks() {
        int linkColor = 0xFF0099DD;

        checkBoxServices.setText(
                RegisterTextLinks.build(
                        checkBoxServices.getText().toString(),
                        linkColor,
                        () -> RegisterNavigator.toTerms(this),
                        () -> RegisterNavigator.toPolicy(this),
                        () -> RegisterNavigator.toLogin(this),
                        false
                )
        );
        checkBoxServices.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        buttonLoginPager.setText(
                RegisterTextLinks.build(
                        buttonLoginPager.getText().toString(),
                        linkColor,
                        () -> RegisterNavigator.toTerms(this),
                        () -> RegisterNavigator.toPolicy(this),
                        () -> RegisterNavigator.toLogin(this),
                        true
                )
        );
        buttonLoginPager.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forumScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // RegisterController.Ui
    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNavigateToLogin() {
        RegisterNavigator.toLogin(this);
    }
}
