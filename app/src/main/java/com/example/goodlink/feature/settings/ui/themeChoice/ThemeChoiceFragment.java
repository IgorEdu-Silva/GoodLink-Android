package com.example.goodlink.feature.settings.ui.themeChoice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.goodlink.R;

public class ThemeChoiceFragment extends Fragment {
    private ToggleButton btnChoiceTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_theme_choice, container, false);

        btnChoiceTheme = view.findViewById(R.id.btnChoiceTheme);

        int mode = AppCompatDelegate.getDefaultNightMode();
        boolean isNight = (mode == AppCompatDelegate.MODE_NIGHT_YES);
        btnChoiceTheme.setChecked(isNight);

        btnChoiceTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        return view;
    }
}