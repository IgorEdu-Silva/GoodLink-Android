package com.example.goodlink.PopUp;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.customDialogStyle);
        setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pop_up_description, container, false);

        ConstraintLayout popUpDescription = view.findViewById(R.id.popUpDescription);

        TextView fullDescriptionTextView = view.findViewById(R.id.fullDescription);
        Button closeButton = view.findViewById(R.id.closeButton);

        int nightModeFlags = getContext().getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case android.content.res.Configuration.UI_MODE_NIGHT_YES:
                fullDescriptionTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                fullDescriptionTextView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_background));
                popUpDescription.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_background));
                break;

            case android.content.res.Configuration.UI_MODE_NIGHT_NO:
                fullDescriptionTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                fullDescriptionTextView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_background));
                popUpDescription.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_background));
                break;

            case android.content.res.Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        closeButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.light_blue));

        if (getArguments() != null) {
            fullDescriptionText = getArguments().getString(FULL_DESCRIPTION_KEY);
            Log.d("PopUpDescription", "Full Description: " + fullDescriptionText);
        }

        if (fullDescriptionTextView != null) {
            fullDescriptionTextView.setText(fullDescriptionText);
            Log.d("PopUpDescription", "TextView set with text: " + fullDescriptionText);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int dialogWidth = (int) (screenWidth * 0.9);
        int dialogHeight = (int) (screenHeight * 0.3);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }
}