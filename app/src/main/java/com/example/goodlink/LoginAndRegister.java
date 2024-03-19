package com.example.goodlink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
            // Se já estiver logado, redirecionar para a próxima tela (ex: MainActivity)
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
                // Se todos os campos estiverem preenchidos, faça o login
                EditText editTextEmail = findViewById(R.id.editTextEmail);
                EditText editTextPassword = findViewById(R.id.editTextPassword);

                String email = editTextEmail.getText().toString();
                String senha = editTextPassword.getText().toString();

                CheckBox checkBoxLog = findViewById(R.id.checkBoxLog);
                boolean manterLogado = checkBoxLog.isChecked();
                mAuthenticator.loginUser(email, senha, new AuthenticationListener() {
                    @Override
                    public void onLoginSuccess(FirebaseUser user) {
                        // Se o login for bem-sucedido, redirecione o usuário para a próxima tela
                        Intent intent = new Intent(LoginAndRegister.this, forum.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onLoginFailure(String errorMessage) {
                        // Se o login falhar, exiba um Toast informando o usuário
                        Toast.makeText(LoginAndRegister.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                if (manterLogado) {
                    // Se o usuário deseja manter-se logado, salvar o estado de login
                    sessionManager.setLogin(true);
                }
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