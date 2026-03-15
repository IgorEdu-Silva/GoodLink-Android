package com.example.goodlink.feature.intro.ui.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.goodlink.R;

public class IntroductionContainerFragment extends Fragment {
    private ViewPager2 viewPager;
    private IntroductionPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction_page, container, false);

        viewPager = view.findViewById(R.id.ViewPagerIntroduction);
        adapter = new IntroductionPagerAdapter(this);
        viewPager.setAdapter(adapter);

        return view;
    }
}