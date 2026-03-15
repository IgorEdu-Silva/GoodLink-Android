package com.example.goodlink.feature.auth.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goodlink.R;
import com.example.goodlink.feature.legal.ui.PolicyActivity;
import com.example.goodlink.feature.legal.ui.TermsAcitivity;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class FragmentPageUserPrivacy extends Fragment {
    private ImageButton btnBackPrivacy;
    private TextView termsOfService, privacity;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch btnPrivacyToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_user_privacy, container, false);

        initUi(view);
        setupListener();

        return view;
    }

    private void initUi(View view) {
        btnBackPrivacy = view.findViewById(R.id.btnBackPrivacy);
        btnPrivacyToggle = view.findViewById(R.id.btnPrivacyToggle);
        termsOfService = view.findViewById(R.id.termsOfService);
        privacity = view.findViewById(R.id.privacity);

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean enabled = prefs.getBoolean("crashlytics_enabled", true);
        btnPrivacyToggle.setChecked(enabled);
        crashlytics.setCrashlyticsCollectionEnabled(enabled);
    }

    private void setupListener() {
        btnBackPrivacy.setOnClickListener(v -> {
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

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        btnPrivacyToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            crashlytics.setCrashlyticsCollectionEnabled(isChecked);
            prefs.edit().putBoolean("crashlytics_enabled", isChecked).apply();
            String msg = isChecked ? "Crashlytics Habilitado" : "Crashlytics Desabilitado";
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });

        termsOfService.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TermsAcitivity.class);
            intent.putExtra("fromFragment", true);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.scale_in_center, R.anim.scale_out_center);
        });

        privacity.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PolicyActivity.class);
            intent.putExtra("fromFragment", true);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.scale_in_center, R.anim.scale_out_center);
        });
    }
}
