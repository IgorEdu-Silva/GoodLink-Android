package com.example.goodlink.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.goodlink.Fragments.FragmentPageAboutProject;
import com.example.goodlink.Fragments.FragmentPageFontSize;
import com.example.goodlink.Fragments.FragmentPageThemeChoice;

public class AdapterPagesIntroduction extends FragmentStateAdapter {
    public AdapterPagesIntroduction(Fragment fragment){
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position){
        switch (position) {
            case 0:
                return new FragmentPageAboutProject();
            case 1:
                return new FragmentPageFontSize();
            case 2:
                return new FragmentPageThemeChoice();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
