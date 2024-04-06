package com.example.goodlink.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goodlink.FireBase.FireBaseAuthenticate;
import com.example.goodlink.FireBase.FireBaseDataBase;
import com.example.goodlink.FireBase.FireStoreDataManager;
import com.example.goodlink.Screens.LoginAndRegister;
import com.example.goodlink.R;
import com.example.goodlink.FireBase.SessionManager;
import com.example.goodlink.FireBase.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TabUsersFragment extends Fragment {
    private FireBaseAuthenticate mAuthenticator;
    private SessionManager sessionManager;
    private TextView usernameTextView;
    private EditText emailEditText;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private User loggedInUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_users, container, false);

        mAuthenticator = new FireBaseAuthenticate(new FireBaseDataBase());

        usernameTextView = view.findViewById(R.id.username_Users);
        emailEditText = view.findViewById(R.id.emailUser_Users);
        Button buttonDeslogar = view.findViewById(R.id.button);

        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            fireStoreDataManager.getUser(currentUser.getUid(), new FireStoreDataManager.FireStoreDataListener<User>() {
                @Override
                public void onSuccess(User userData) {
                    loggedInUser = userData;
                    String username = userData.getNome();
                    String email = userData.getEmail();
                    usernameTextView.setText(username);
                    emailEditText.setText(email);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

        buttonDeslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setLogin(false);

                Intent intent = new Intent(getActivity(), LoginAndRegister.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Button btnSave = view.findViewById(R.id.btnSave_Users);
        EditText etOldPassword = view.findViewById(R.id.password_Users);
        EditText etNewPassword = view.findViewById(R.id.NewPassword_Users);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();

                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);
                    currentUser.reauthenticate(credential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    currentUser.updatePassword(newPassword)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(requireContext(), "Senha atualizada com sucesso.", Toast.LENGTH_SHORT).show();
                                                    etOldPassword.setText("");
                                                    etNewPassword.setText("");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(requireContext(), "Falha ao atualizar a senha: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireContext(), "Falha ao reautenticar o usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        clearEditTexts();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearEditTexts();
    }

    private void clearEditTexts() {
        if (etNewPassword != null) {
            etNewPassword.setText("");
        }
        if (etOldPassword != null) {
            etOldPassword.setText("");
        }
    }
}