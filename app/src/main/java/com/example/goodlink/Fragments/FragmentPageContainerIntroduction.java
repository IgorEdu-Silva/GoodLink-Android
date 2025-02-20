package com.example.goodlink.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.goodlink.Adapter.AdapterPagesIntroduction;
import com.example.goodlink.R;

public class FragmentPageContainerIntroduction extends Fragment {
    private ViewPager2 viewPager;
    private AdapterPagesIntroduction adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction_page, container, false);

        viewPager = view.findViewById(R.id.ViewPagerIntroduction);
        adapter = new AdapterPagesIntroduction(this);
        viewPager.setAdapter(adapter);

        return view;
    }
}