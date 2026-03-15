package com.example.goodlink.feature.forum.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.goodlink.feature.auth.ui.settings.FragmentPageUserSettings;

public class AdapterPagerForum extends FragmentStateAdapter {
    public AdapterPagerForum(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentPageRepository();
            case 1:
                return new FragmentPageUserSettings();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}