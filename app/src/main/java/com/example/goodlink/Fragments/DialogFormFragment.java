package com.example.goodlink.Fragments;

import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.goodlink.R;

public class DialogFormFragment extends DialogFragment {
    private ImageButton btnBackForm;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBackForm = view.findViewById(R.id.btnBackForm);
        Button btnSend = view.findViewById(R.id.btnSend_Form);
        int colorBtn = ContextCompat.getColor(requireContext(), R.color.light_blue);
        int textColorBtn = ContextCompat.getColor(requireContext(), isDarkMode() ? R.color.white : R.color.black);
        btnSend.setTextColor(textColorBtn);
        btnSend.setBackgroundTintList(ColorStateList.valueOf(colorBtn));

        Spinner spinner = view.findViewById(R.id.categories);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.categories,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        setSpinnerTextColor(spinner);

        int textColor = ContextCompat.getColor(requireContext(), R.color.black);

        if (view instanceof TextView) {
            ((TextView) spinner.getSelectedView()).setTextColor(textColor);
        }

        spinner.post(() -> {
           View selectedView = spinner.getSelectedView();
           if (selectedView instanceof TextView) {
                ((TextView) spinner.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
           }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerTextColor(spinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        view.post(() -> updateSpinnerOnThemeChange(spinner));

        btnBackForm.setOnClickListener(v -> dismiss());
    }

    private void updateSpinnerOnThemeChange(Spinner spinner) {
        spinner.post(() -> {
            int position = spinner.getSelectedItemPosition();

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.categories,
                    android.R.layout.simple_spinner_item
            );

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(position, true);

            setSpinnerTextColor(spinner);
        });
    }


    private void setSpinnerTextColor(Spinner spinner) {
        int color = getResources().getColor(getSpinnerTextColor());
        View selectedView = spinner.getSelectedView();
        if (selectedView instanceof TextView) {
            ((TextView) selectedView).setTextColor(color);
        }
    }

    private int getSpinnerTextColor() {
        if (isDarkMode()) {
            return R.color.white;
        } else {
            return R.color.black;
        }
    }

    private boolean isDarkMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            UiModeManager uiModeManager = (UiModeManager) requireContext().getSystemService(Context.UI_MODE_SERVICE);
            int currentMode = uiModeManager.getNightMode();
            return currentMode == UiModeManager.MODE_NIGHT_YES;
        } else {
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_form, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Spinner spinner = getView().findViewById(R.id.categories);
        setSpinnerTextColor(spinner);
    }

}
