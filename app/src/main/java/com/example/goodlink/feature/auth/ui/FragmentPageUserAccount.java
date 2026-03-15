package com.example.goodlink.feature.auth.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goodlink.feature.auth.ui.settings.FragmentPageUserSettings;
import com.example.goodlink.infrastructure.navigation.HelperNavigateToFragment;
import com.example.goodlink.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.UserInfo;

public class FragmentPageUserAccount extends Fragment {
    private ImageButton btnBackAccount;
    private TextView accountUserName, accountUserNamePreview, accountEmail, accountEmailPreview, accountPassword, deleteAccount;
    private ActivityResultLauncher<Intent> googleReauthLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_user_account, container, false);

        initUi(view);
        setupListener();
        populateUserData();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleReauthLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            String idToken = task.getResult(ApiException.class).getIdToken();
                            AuthCredential cred = GoogleAuthProvider.getCredential(idToken, null);
                            FirebaseAuth.getInstance().getCurrentUser()
                                    .reauthenticate(cred)
                                    .addOnCompleteListener(r -> performAccountDeletion());
                        } catch (ApiException ex) {
                            Toast.makeText(requireContext(), "Reautenticação Google falhou", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void initUi(View view) {
        accountUserName = view.findViewById(R.id.accountUserName);
        accountUserNamePreview = view.findViewById(R.id.accountUserNamePreview);
        accountEmail = view.findViewById(R.id.accountEmail);
        accountEmailPreview = view.findViewById(R.id.accountEmailPreview);
        accountPassword = view.findViewById(R.id.accountPassword);
        deleteAccount = view.findViewById(R.id.deleteAccount);
        btnBackAccount = view.findViewById(R.id.btnBackAccount);
    }

    private void setupListener() {
        btnBackAccount.setOnClickListener(v -> HelperNavigateToFragment.navigateBack(requireActivity(), new FragmentPageUserSettings(), "FragmentPageUserSettings"));

        accountPassword.setOnClickListener(view -> HelperNavigateToFragment.navigateTo(requireActivity(), new FragmentPageUserAccountResetPassword(), "AccountResetPassword", true));

        Context context = new ContextThemeWrapper(requireContext(), R.style.CustomAlertDialog);

        deleteAccount.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmar exclusão")
                    .setMessage("Tem certeza de que deseja excluir sua conta? Esta ação é irreversível.")
                    .setPositiveButton("Excluir", (dialog, which) -> performAccountDeletion())
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.border);
            }
            dialog.show();
        });
    }

    private void performAccountDeletion() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                requireActivity().finish();
            }
        }).addOnFailureListener(e -> {
            if (e instanceof FirebaseAuthRecentLoginRequiredException) {
                reauthenticateAndDelete(user);
            } else {
                Toast.makeText(requireContext(), "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reauthenticateAndDelete(FirebaseUser user) {
        for (UserInfo info : user.getProviderData()) {
            String provider = info.getProviderId();
            if (GoogleAuthProvider.PROVIDER_ID.equals(provider)) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient googleClient = GoogleSignIn.getClient(requireContext(), gso);
                Intent signInIntent = googleClient.getSignInIntent();
                googleReauthLauncher.launch(signInIntent);
                return;
            }
            if (GithubAuthProvider.PROVIDER_ID.equals(provider)) {
                OAuthProvider.Builder providerBuilder = OAuthProvider.newBuilder("github.com");
                FirebaseAuth.getInstance()
                        .startActivityForSignInWithProvider(
                                requireActivity(), providerBuilder.build()
                        )
                        .addOnSuccessListener(authResult -> performAccountDeletion())
                        .addOnFailureListener(err -> Toast.makeText(requireContext(), "Falha ao reautenticar via GitHub", Toast.LENGTH_SHORT).show());
                return;
            }
        }

        promptEmailPasswordReauth(user);
    }

    private void promptEmailPasswordReauth(FirebaseUser user) {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(requireContext())
                .setTitle("Reautenticar")
                .setMessage("Digite sua senha atual:")
                .setView(input)
                .setPositiveButton("OK", (d, w) -> {
                    String pwd = input.getText().toString();
                    AuthCredential cred = EmailAuthProvider.getCredential(user.getEmail(), pwd);
                    user.reauthenticate(cred)
                            .addOnCompleteListener(r -> performAccountDeletion())
                            .addOnFailureListener(e -> Toast.makeText(requireContext(), "Senha incorreta", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void populateUserData() {
        Log.d("FragmentDebug", "populateUserData chamado");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("FragmentDebug", "user: " + user);

        if (user != null) {
            String userName = user.getDisplayName();
            String email = user.getEmail();

            Log.d("FragmentDebug", "Nome: " + userName + ", Email: " + email);

            accountUserNamePreview.setText(userName != null ? userName : "Nome não disponível");
            accountEmailPreview.setText(email != null ? email : "Email não disponível");
        } else {
            accountUserNamePreview.setText("Usuário não logado");
            accountEmailPreview.setText("Usuário não logado");
        }
    }

}
