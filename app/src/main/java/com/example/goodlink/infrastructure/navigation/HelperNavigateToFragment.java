package com.example.goodlink.infrastructure.navigation;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.goodlink.R;

public class HelperNavigateToFragment {
    public static void navigateTo(FragmentActivity activity, Fragment fragment, String tag, boolean addToBackStack) {
        View container = activity.findViewById(R.id.containerAccount);

        if (container.getVisibility() != View.VISIBLE)
            container.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.scale_in_center,
                        R.anim.scale_out_center
                )
                .replace(R.id.containerAccount, fragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();

        addBackStackVisibilityListener(activity);
    }

    public static void navigateBack(FragmentActivity activity, Fragment fragment, String tag) {
        View container = activity.findViewById(R.id.containerAccount);

        FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.scale_in_reverse,
                        R.anim.scale_out_reverse
                )
                .replace(R.id.containerAccount, fragment, tag)
                .addToBackStack(tag);

        transaction.commit();

        container.postDelayed(() -> container.setVisibility(View.INVISIBLE), 300);
    }

    private static void addBackStackVisibilityListener(FragmentActivity activity) {
        View container = activity.findViewById(R.id.containerAccount);
        FragmentManager manager = activity.getSupportFragmentManager();

        manager.addOnBackStackChangedListener(() -> {
            Fragment current = manager.findFragmentById(R.id.containerAccount);
            if (current == null && container.getVisibility() == View.VISIBLE) {
                container.setVisibility(View.INVISIBLE);
            }
        });
    }
}