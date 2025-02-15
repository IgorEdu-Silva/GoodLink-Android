package com.example.goodlink.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.goodlink.R;
import com.example.goodlink.Utils.FontSizeUtils;

public class FragmentPageFontSize extends Fragment {
    private TextView fontSizeLetter;
    private SeekBar chanceFontSizeLetter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_font_size, container, false);
        chanceFontSizeLetter = view.findViewById(R.id.chanceFontSizeLetter);
        fontSizeLetter = view.findViewById(R.id.fontSizeLetter);

        float fontSize = FontSizeUtils.getFontSize(requireContext());

        fontSizeLetter.setTextSize(fontSize);
        chanceFontSizeLetter.setProgress((int) fontSize);

        FontSizeUtils.applySpecificFontSize(fontSizeLetter, fontSize);

        chanceFontSizeLetter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 10) progress = 10;
                fontSizeLetter.setTextSize(progress);
                FontSizeUtils.saveFontSize(requireContext(), progress);
                FontSizeUtils.applySpecificFontSize(fontSizeLetter, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

}