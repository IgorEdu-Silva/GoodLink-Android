package com.example.goodlink;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TabUsersFragment extends Fragment {

    private FireBaseAuthenticate mAuthenticator;
    private SessionManager sessionManager;
    private TextView usernameTextView;
    private EditText emailEditText;
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

        mAuthenticator = new FireBaseAuthenticate(new FireBaseDataBase()); // Inicializa a autenticação Firebase

        usernameTextView = view.findViewById(R.id.username_Users);
        emailEditText = view.findViewById(R.id.emailUser_Users);
        Button buttonDeslogar = view.findViewById(R.id.button);

        // Recupera os dados do usuário do Firestore
        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Obtém o usuário atualmente autenticado diretamente do Firebase Authentication
        if (currentUser != null) {
            fireStoreDataManager.getUser(currentUser.getUid(), new FireStoreDataManager.FireStoreDataListener<User>() {
                @Override
                public void onSuccess(User userData) {
                    // Se os dados do usuário foram recuperados com sucesso, defina o nome de usuário e email nos componentes de interface do usuário
                    loggedInUser = userData;
                    String username = userData.getNome();
                    String email = userData.getEmail();
                    usernameTextView.setText(username);
                    emailEditText.setText(email);
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Se houver uma falha ao recuperar os dados do usuário, exiba uma mensagem de erro
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

        return view;
    }

}