package com.example.goodlink.feature.auth.ui.settings.support;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.goodlink.R;
import com.example.goodlink.feature.auth.ui.settings.FragmentPageUserSettings;

public class FragmentPageUserSupport extends Fragment {
    ImageButton btnBackSupport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_user_support, container, false);

        initUi(view);
        setupListener();

        return view;
    }

    private void initUi(View view) {
        btnBackSupport = view.findViewById(R.id.btnBackSupport);

    }

    private void setupListener() {
        btnBackSupport.setOnClickListener(v -> {
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