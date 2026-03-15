package com.example.goodlink.feature.auth.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.goodlink.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class FragmentPageUserAccountResetPassword extends Fragment {
    private ImageButton btnBackAccountResetPassword;
    private Button btnConfirmAccountResetPassword;
    private EditText inputCurrentPassword, inputNewPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_user_account_reset_password, container, false);

        initUI(view);
        hideUI(view);
        setupListener();

        return view;
    }

    private void initUI(View view) {
        btnBackAccountResetPassword = view.findViewById(R.id.btnBackAccountResetPassword);
        btnConfirmAccountResetPassword = view.findViewById(R.id.btnConfirmAccountResetPassword);
        inputCurrentPassword = view.findViewById(R.id.inputCurrentPassword);
        inputNewPassword = view.findViewById(R.id.inputNewPassword);
        btnConfirmAccountResetPassword.setEnabled(false);
    }

    private void hideUI(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean hasPasswordProvider = false;
        if (user != null) {
            for (UserInfo info : user.getProviderData()) {
                if (EmailAuthProvider.PROVIDER_ID.equals(info.getProviderId())) {
                    hasPasswordProvider = true;
                    break;
                }
            }
        }
        if (!hasPasswordProvider) {
            view.findViewById(R.id.inputCurrentPassword).setEnabled(false);
            view.findViewById(R.id.inputNewPassword).setEnabled(false);
            Toast.makeText(requireContext(), "Redefinição de senha não disponível para o seu login", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListener() {
        btnBackAccountResetPassword.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                checkPasswordMatchAndEnableButton();
            }
        };

        inputCurrentPassword.addTextChangedListener(passwordTextWatcher);
        inputNewPassword.addTextChangedListener(passwordTextWatcher);

        btnConfirmAccountResetPassword.setOnClickListener(v -> {
            String newPassword = inputNewPassword.getText().toString().trim();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                user.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Erro ao alterar a senha", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void checkPasswordMatchAndEnableButton() {
        String currentPassword = inputCurrentPassword.getText().toString().trim();
        String newPassword = inputNewPassword.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || currentPassword.isEmpty() || newPassword.isEmpty()) {
            btnConfirmAccountResetPassword.setEnabled(false);
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            boolean valid = task.isSuccessful();
            btnConfirmAccountResetPassword.setEnabled(valid && !currentPassword.equals(newPassword));
        });
    }
}