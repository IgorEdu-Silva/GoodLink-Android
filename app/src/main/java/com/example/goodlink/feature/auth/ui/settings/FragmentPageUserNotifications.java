package com.example.goodlink.feature.auth.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.goodlink.R;

public class FragmentPageUserNotifications extends Fragment {
    ImageButton btnBackNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_user_notifications, container, false);

        initUi(view);
        setupListener();

        return view;
    }

    private void initUi(View view) {
        btnBackNotifications = view.findViewById(R.id.btnBackNotifications);

    }

    private void setupListener() {
        btnBackNotifications.setOnClickListener(v -> {
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
    }
}