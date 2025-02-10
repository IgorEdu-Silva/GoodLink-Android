package com.example.goodlink.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.goodlink.Fragments.TabRepositoryFragment;
import com.example.goodlink.Fragments.UserSettingsFragment;

public class AdapterPagerFragments extends FragmentStateAdapter {
    public AdapterPagerFragments(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TabRepositoryFragment();
            case 1:
                return new UserSettingsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}