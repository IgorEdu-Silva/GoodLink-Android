package com.example.goodlink.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.goodlink.R;

public class UserSettingsFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        searchConfig = view.findViewById(R.id.searchConfig);
        searchConfig.setFocusable(true);
        searchConfig.setFocusableInTouchMode(true);
        searchConfig.setIconifiedByDefault(false);

        return view;
    }
}