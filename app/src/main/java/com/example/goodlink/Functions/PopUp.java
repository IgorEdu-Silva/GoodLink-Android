package com.example.goodlink.Functions;

import androidx.fragment.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.goodlink.R;

public class PopUp extends DialogFragment {
    private String fullDescriptionText;
    private static final String FULL_DESCRIPTION_KEY = "fullDescription";

    public PopUp() {
    }

    public static PopUp newInstance(String fullDescription) {
        PopUp fragment = new PopUp();
        Bundle args = new Bundle();
        args.putString(FULL_DESCRIPTION_KEY, fullDescription);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pop_up, container, false);

        TextView fullDescriptionTextView = view.findViewById(R.id.fullDescription);

        if (getArguments() != null) {
            fullDescriptionText = getArguments().getString(FULL_DESCRIPTION_KEY);
        }

        if (fullDescriptionTextView != null) {
            fullDescriptionTextView.setText(fullDescriptionText);
        }

        fullDescriptionTextView.setVisibility(View.VISIBLE);

        return view;
    }

}