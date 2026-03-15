package com.example.goodlink.feature.intro.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.goodlink.feature.about.ui.AboutProject.AboutProjectFragment;
import com.example.goodlink.feature.settings.ui.fontSize.FontSizeFragment;
import com.example.goodlink.feature.settings.ui.themeChoice.ThemeChoiceFragment;

public class IntroductionPagerAdapter extends FragmentStateAdapter {
    public IntroductionPagerAdapter(@NonNull Fragment fragment){
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position){
        switch (position) {
            case 0:
                return new AboutProjectFragment();
            case 1:
                return new FontSizeFragment();
            case 2:
                return new ThemeChoiceFragment();
            default: throw new IllegalArgumentException("Invalid position" + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
