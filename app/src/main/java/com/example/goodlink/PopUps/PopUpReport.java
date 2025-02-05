package com.example.goodlink.PopUps;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.goodlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PopUpReport extends DialogFragment {
    private static final String ARG_REPOSITORY_ID = "repositoryId";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button sendReport;
    private String repositoryId;
    private RadioGroup optionsReport;
    private RadioButton firstOption, secondOption, thirdOption, forthOption, fifthOption;

    public static PopUpReport newInstance(String repositoryId) {
        PopUpReport fragment = new PopUpReport();
        Bundle args = new Bundle();
        args.putString(ARG_REPOSITORY_ID, repositoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.customDialogStyle);
        setCancelable(false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            repositoryId = getArguments().getString(ARG_REPOSITORY_ID);
            if (repositoryId == null) {
                throw new IllegalArgumentException("repositoryId cannot be null");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pop_up_report, container, false);

        ConstraintLayout popUpReport = view.findViewById(R.id.popUpReport);
        optionsReport = view.findViewById((R.id.optionsReport));
        firstOption = view.findViewById(R.id.firstOption);
        secondOption = view.findViewById(R.id.secondOption);
        thirdOption = view.findViewById(R.id.thirdOption);
        forthOption = view.findViewById(R.id.forthOption);
        fifthOption = view.findViewById(R.id.fifthOption);

        Button closeButton = view.findViewById(R.id.closeButton);
        sendReport = view.findViewById(R.id.sendReport);
        sendReport.setEnabled(false);
        sendReport.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));

        int nightModeFlags = getContext().getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case android.content.res.Configuration.UI_MODE_NIGHT_YES:
                popUpReport.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_background));
                firstOption.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                secondOption.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                thirdOption.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                forthOption.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                fifthOption.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                optionsReport.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_background));
                closeButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                break;

            case android.content.res.Configuration.UI_MODE_NIGHT_NO:
                popUpReport.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_background));
                firstOption.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                secondOption.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                thirdOption.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                forthOption.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                fifthOption.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                optionsReport.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_background));
                closeButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                break;

            case android.content.res.Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        optionsReport.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendReport.setEnabled(true);
                sendReport.setTextColor(ContextCompat.getColor(getContext(), nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES ?
                        R.color.white : R.color.black));
            }
        });

        closeButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.light_blue));
        sendReport.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.light_blue));

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport();
            }
        });

        return view;
    }

    private void sendReport() {
        int selectedId = optionsReport.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(getContext(), "Por favor, selecione uma opção para o relatório.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = getView().findViewById(selectedId);
        String reportText = selectedRadioButton.getText().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Nenhum usuário logado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Nome não definido";
        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "Email não definido";

        Map<String, Object> report = new HashMap<>();
        report.put("reportText", reportText);
        report.put("repositoryId", repositoryId);
        report.put("userName", userName);
        report.put("userEmail", userEmail);

        db.collection("reports")
                .add(report)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Relatório enviado com sucesso!", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Falha ao enviar relatório. Tente novamente.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int dialogWidth = (int) (screenWidth * 0.9);
        int dialogHeight = (int) (screenHeight * 0.4);

        if (screenWidth > 1080 && screenHeight > 2400) {
            dialogHeight = (int) (screenHeight * 0.3);
        }

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }
}