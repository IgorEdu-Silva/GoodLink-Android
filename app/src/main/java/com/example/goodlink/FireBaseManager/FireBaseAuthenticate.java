package com.example.goodlink.FireBaseManager;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.goodlink.R;
import com.example.goodlink.Screens.Forum;
import com.example.goodlink.Screens.Login;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;


public class FireBaseAuthenticate {
    private final FirebaseAuth mAuth;
    private final FireBaseDataBase mDatabase;
    private ManagerSession managerSession;
    private GoogleSignInClient mGoogleSignInClient;

    public FireBaseAuthenticate(FireBaseDataBase database) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = database;
    }

    public FireBaseAuthenticate(FireBaseDataBase database, Context context) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = database;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void loginUser(String email, String password, final Login.AuthenticationListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                listener.onLoginSuccess(user);
                            } else {
                                listener.onLoginFailure("Por favor, verifique seu email para fazer login.");
                                mAuth.signOut();
                            }
                        }
                    } else {
                        listener.onLoginFailure("Usuário não cadastrado");
                    }
                });
    }

    public void registerUser(String nome, String email, String password, final Context context, final RegistrationCallback callback) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Registre-se para poder entrar.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nome)
                                        .build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
                                            fireStoreDataManager.addUser(user.getUid(), nome, email);

                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "E-mail de verificação enviado.", Toast.LENGTH_SHORT).show();

                                                        if (callback != null) {
                                                            callback.onRegistrationSuccess();
                                                        }
                                                    } else {
                                                        Toast.makeText(context, "Falha ao enviar e-mail de verificação.", Toast.LENGTH_SHORT).show();

                                                        if (callback != null) {
                                                            callback.onRegistrationFailure("Falha ao enviar e-mail de verificação.");
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(context, "Falha ao atualizar o nome de usuário.", Toast.LENGTH_SHORT).show();

                                            if (callback != null) {
                                                callback.onRegistrationFailure("Falha ao atualizar o nome de usuário.");
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(context, "Registro falhou.", Toast.LENGTH_SHORT).show();

                            if (callback != null) {
                                callback.onRegistrationFailure("Registro falhou.");
                            }
                        }
                    }
                });
    }

    public void resetPassword(String email, ResetPasswordListener context) {
        mDatabase.checkIfEmailExists(email, new FireBaseDataBase.EmailCheckListener() {
            @Override
            public void onEmailExists(boolean exists) {
                if (exists) {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        context.onResetSuccess();
                                    } else {

                                        String errorMessage = task.getException().getMessage();
                                        context.onResetFailure(errorMessage);
                                    }
                                }
                            });
                } else {
                    String errorMessage = "O email não está cadastrado.";
                    context.onResetFailure(errorMessage);
                }
            }
        });
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public void signInWithGoogle(String idToken, final GoogleSignInCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
                            fireStoreDataManager.addUser(user.getUid(), user.getDisplayName(), user.getEmail());

                            callback.onSuccess(user);
                        }
                    } else {
                        callback.onFailure("Autenticação com Google falhou.");
                    }
                });
    }

    public void signInWithGitHub(Context context) {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");

        FirebaseAuth.getInstance()
                .startActivityForSignInWithProvider((Activity) context, provider.build())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    Log.d(TAG, "Usuário autenticado: " + (user != null ? user.getDisplayName() : "Usuário desconhecido"));
                    managerSession.setLogin(true);
                    goToActivity(context, Forum.class);
                })
                .addOnFailureListener(e -> logErrorException("Method signInWithGitHub on Login.class", "Erro ao autenticar com GitHub: ", e));
    }

    public void signInAnonymous(final Context context){
        mAuth.signInAnonymously()
                .addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null){
                            goToActivity(context, Forum.class);
                        }
                    } else {
                        Toast.makeText(context, "Falha ao ser conectar anonimamente.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface GoogleSignInCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    public interface ResetPasswordListener {
        void onResetSuccess();
        void onResetFailure(String errorMessage);
    }

    public interface RegistrationCallback {
        void onRegistrationSuccess();
        void onRegistrationFailure(String errorMessage);
    }

    private void goToActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    private void logErrorException(String tag, String error, Exception e){
        Log.e(tag, error, e);
    }
}
