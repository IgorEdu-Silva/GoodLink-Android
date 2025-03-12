package com.example.goodlink.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.goodlink.FireBaseManager.ManagerSession;
import com.example.goodlink.R;
import com.example.goodlink.Screens.Login;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentPageUserSettings extends Fragment {
    private SearchView searchConfig;
    private ImageView ico_config_account;
    private TextView text_config_account;
    private ImageView ico_config_security;
    private TextView text_config_security;
    private ImageView ico_config_theme;
    private TextView text_config_theme;
    private ImageView icon_config_accessibility;
    private TextView text_config_accessibility;
    private ImageView ico_config_notifications;
    private TextView text_config_notifications;
    private ImageView ico_config_support;
    private TextView text_config_support;
    private Button btnLogoutConfig;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_user_settings, container, false);
        searchConfig = view.findViewById(R.id.searchConfig);
        btnLogoutConfig = view.findViewById(R.id.btnLogoutConfig);

        initUI();
        setupListener();

        return view;
    }

    private void initUI() {
        searchConfig.setFocusable(true);
        searchConfig.setFocusableInTouchMode(true);
        searchConfig.setIconifiedByDefault(false);
    }

    private void setupListener() {
        btnLogoutConfig.setOnClickListener(view -> logoutAccount());
    }

    private void logoutAccount() {
        FirebaseAuth.getInstance().signOut();
        viewLogD("LogoutUserAccount", "User logged in successfully");
        ManagerSession managerSession = new ManagerSession(requireActivity());
        managerSession.setLogin(false);

        Intent intent = new Intent(requireActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void viewLogD(String tag, String message){
        Log.d(tag, message);
    }

    private void viewLogE(String tag, String message){
        Log.e(tag, message);
    }
}