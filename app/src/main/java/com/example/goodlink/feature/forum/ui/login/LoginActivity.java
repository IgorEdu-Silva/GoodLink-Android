package com.example.goodlink.feature.forum.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goodlink.R;
import com.example.goodlink.infrastructure.fcm.FCMMessagingService;
import com.example.goodlink.infrastructure.fcm.FcmTokenProvider;
import com.example.goodlink.infrastructure.firebase.auth.FireBaseAuthenticate;
import com.example.goodlink.infrastructure.firebase.database.FireBaseDataBase;
import com.example.goodlink.infrastructure.session.LoginStateStorage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity implements LoginController.Ui {

    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private FireBaseAuthenticate authenticator;
    private LoginStateStorage loginStateStorage;
    private FcmTokenProvider fcmTokenProvider;
    private LoginController controller;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView btnRegisterPage;
    private TextView forgotPass;
    private CheckBox checkBoxLog;
    private Button btnLogin;
    private ImageButton btnGoogle;
    private ImageButton btnGitHub;
    private ImageButton btnAnonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initUI();
        setupInsets();
        setupLinks();
        setupServices();
        setupListeners();
    }

    private void initUI() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxLog = findViewById(R.id.checkBoxLog);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterPage = findViewById(R.id.btnRegisterPage);
        forgotPass = findViewById(R.id.forgotePass);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnGitHub = findViewById(R.id.btnGitHub);
        btnAnonymous = findViewById(R.id.btnAnonymous);
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forumScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupLinks() {
        int linkColor = 0xFF0099DD;

        btnRegisterPage.setText(
                LoginTextLinks.buildRegisterLink(
                        btnRegisterPage.getText().toString(),
                        linkColor,
                        () -> LoginNavigator.toRegister(this)
                )
        );
        btnRegisterPage.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupServices() {
        FireBaseDataBase database = new FireBaseDataBase();
        authenticator = new FireBaseAuthenticate(database, this);

        loginStateStorage = new LoginStateStorage(this);
        fcmTokenProvider = new FcmTokenProvider(getApplicationContext(), new FCMMessagingService());

        controller = new LoginController(authenticator, loginStateStorage, fcmTokenProvider, this);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v ->
                controller.loginWithEmail(
                        editTextEmail.getText().toString().trim(),
                        editTextPassword.getText().toString(),
                        checkBoxLog.isChecked()
                )
        );

        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        btnGitHub.setOnClickListener(v -> authenticator.signInWithGitHub(this));

        btnAnonymous.setOnClickListener(v -> authenticator.signInAnonymous(this));

        forgotPass.setOnClickListener(v -> LoginNavigator.toResetPass(this));
    }

    private void signInWithGoogle() {
        if (authenticator.getGoogleSignInClient() == null) {
            Log.e("LoginActivity", "GoogleSignInClient não configurado");
            showToast("Erro ao configurar Google Sign-In");
            return;
        }

        Intent signInIntent = authenticator.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        controller.autoRedirectIfLogged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != RC_SIGN_IN_GOOGLE || data == null) return;

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account == null) {
                showToast("Erro ao autenticar com Google");
                return;
            }

            String idToken = account.getIdToken();
            controller.loginWithGoogleToken(idToken);

        } catch (ApiException e) {
            Log.e("LoginActivity", "Erro na autenticação com Google: " + e.getStatusCode(), e);
            showToast("Erro ao se registrar com Google");
        }
    }

    // LoginController.Ui
    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNavigateToForum() {
        LoginNavigator.toForum(this);
    }
}
