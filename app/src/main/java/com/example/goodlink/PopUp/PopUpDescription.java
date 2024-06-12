package com.example.goodlink.PopUp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.goodlink.R;

public class PopUpDescription extends DialogFragment {
    private String fullDescriptionText;
    private static final String FULL_DESCRIPTION_KEY = "fullDescription";

    public PopUpDescription() {
    }

    public static PopUpDescription newInstance(String fullDescription) {
        PopUpDescription fragment = new PopUpDescription();
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