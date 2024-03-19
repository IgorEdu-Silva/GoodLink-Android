package com.example.goodlink;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FireBaseAuthenticate {
    private FirebaseAuth mAuth;
    private FireBaseDataBase mDatabase;

    public FireBaseAuthenticate(FireBaseDataBase database) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = database;
    }

    public void loginUser(String email, String password, final LoginAndRegister.AuthenticationListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Autenticação bem-sucedida, redirecione o usuário para a próxima página
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Se o login for bem-sucedido, chame o método onLoginSuccess do listener
                            listener.onLoginSuccess(user);
                        }
                    } else {
                        // Autenticação falhou, informe o listener
                        listener.onLoginFailure("Usuário não cadastrado");
                    }
                });
    }





    // Dentro do método registerUser da classe FireBaseAuthenticate
    public void registerUser(String nome, String email, String password, final Context context) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // Se o email ou senha estiverem vazios, exiba uma mensagem de erro
            Toast.makeText(context, "Registre-se para poder entrar.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro bem-sucedido, envie e-mail de verificação
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Associe o nome do usuário ao e-mail antes de enviar o e-mail de verificação
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nome)
                                        .build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Nome de usuário atualizado com sucesso
                                            // Envie e-mail de verificação
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // E-mail de verificação enviado com sucesso
                                                        Toast.makeText(context, "E-mail de verificação enviado.", Toast.LENGTH_SHORT).show();
                                                        // Agora, adicione o usuário ao Firestore
                                                        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
                                                        fireStoreDataManager.addUser(user.getUid(), nome, email);
                                                    } else {
                                                        // Falha ao enviar e-mail de verificação
                                                        Toast.makeText(context, "Falha ao enviar e-mail de verificação.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            // Falha ao atualizar o nome de usuário
                                            Toast.makeText(context, "Falha ao atualizar o nome de usuário.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            // O restante do código para salvar os detalhes do usuário no banco de dados permanece o mesmo
                        } else {
                            // Falha no registro
                            Toast.makeText(context, "Registro falhou.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void logoutUser() {
        mAuth.signOut();
    }
}
