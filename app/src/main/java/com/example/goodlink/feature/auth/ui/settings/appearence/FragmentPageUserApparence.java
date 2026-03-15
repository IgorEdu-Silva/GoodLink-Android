package com.example.goodlink.feature.auth.ui.settings.appearence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.goodlink.feature.auth.ui.settings.FragmentPageUserSettings;
import com.example.goodlink.R;

public class FragmentPageUserApparence extends Fragment {
    private ImageButton btnBackApparence, chanceFontSizeLess, chanceFontSizeMore;
    private Button btnResetDefault, btnConfirmChangeFontSize;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch syncThemeToggle;
    private SeekBar chanceFontSize;
    private float pendingFontSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_user_apparence, container, false);

        initUi(view);
        setupListener(view);
        syncTheme();

        return view;
    }

    private void initUi(View view) {
        btnBackApparence = view.findViewById(R.id.btnBackApparence);
        syncThemeToggle = view.findViewById(R.id.syncThemeToggle);
        chanceFontSize = view.findViewById(R.id.chanceFontSize);
        chanceFontSizeLess = view.findViewById(R.id.chanceFontSizeLess);
        chanceFontSizeMore = view.findViewById(R.id.chanceFontSizeMore);
        btnResetDefault = view.findViewById(R.id.ResetDefault);
        btnConfirmChangeFontSize = view.findViewById(R.id.ConfirmChangeFontSize);
    }

    private void setupListener(View rootView) {
        btnBackApparence.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.scale_in_reverse,
                            R.anim.scale_out_reverse
                    )
                    .replace(R.id.containerAccount, new FragmentPageUserSettings(), "UserSettings")
                    .commit();

            View container = requireActivity().findViewById(R.id.containerAccount);
            container.postDelayed(() -> container.setVisibility(View.INVISIBLE), 300);
        });

        syncThemeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveThemeSyncPreference(isChecked);
            applyTheme(isChecked);
            restartApp();
        });

        chanceFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pendingFontSize = progress / 5f + 10;
                updateFontSize(rootView, pendingFontSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        chanceFontSizeLess.setOnClickListener(v -> {
            int current = chanceFontSize.getProgress();
            chanceFontSize.setProgress(Math.max(0, current - 5));
        });

        chanceFontSizeMore.setOnClickListener(v -> {
            int current = chanceFontSize.getProgress();
            chanceFontSize.setProgress(Math.min(100, current + 5));
        });

        btnConfirmChangeFontSize.setOnClickListener(v -> {
            saveFontSize(pendingFontSize);
            restartApp();
        });

        btnResetDefault.setOnClickListener(v -> {
            float defaultFontSize = 14f;
            chanceFontSize.setProgress((int) ((defaultFontSize - 10f) * 5));
            pendingFontSize = defaultFontSize;
            updateFontSize(rootView, pendingFontSize);
            saveFontSize(defaultFontSize);
            restartApp();
        });
    }

    private void syncTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isSyncEnabled = prefs.getBoolean("sync_theme", true);

        syncThemeToggle.setOnCheckedChangeListener(null);
        syncThemeToggle.setChecked(isSyncEnabled);
        syncThemeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveThemeSyncPreference(isChecked);
            applyTheme(isChecked);
            restartApp();
        });

        applyTheme(isSyncEnabled);
    }

    private void saveThemeSyncPreference(boolean isChecked) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putBoolean("sync_theme", isChecked).apply();
    }

    private void applyTheme(boolean isChecked) {
        AppCompatDelegate.setDefaultNightMode(
                isChecked ?
                        ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES ?
                                AppCompatDelegate.MODE_NIGHT_YES :
                                AppCompatDelegate.MODE_NIGHT_NO)
                        : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void restartApp() {
        if (getActivity() == null) return;

        Context context = getActivity().getApplicationContext();
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) return;

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Runtime.getRuntime().exit(0);
    }

    private void saveFontSize(float sizeSp) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putFloat("font_size", sizeSp).apply();
    }

    private float loadFontSize() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        return prefs.getFloat("font_size", 14f);
    }

    private void updateFontSize(View view, float fontSizeSp) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (textView.getAutoSizeTextType() == TextView.AUTO_SIZE_TEXT_TYPE_NONE) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeSp);
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                updateFontSize(group.getChildAt(i), fontSizeSp);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View container = requireActivity().findViewById(R.id.containerAccount);
        if (container != null) {
            container.setVisibility(View.VISIBLE);
        }
    }
}