package com.example.goodlink.PopUp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private EditText reportTextInsert;
    private Button sendReport;
    private String repositoryId;

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

        reportTextInsert  = view.findViewById(R.id.reportTextArea);
        Button closeButton = view.findViewById(R.id.closeButton);
        sendReport = view.findViewById(R.id.sendReport);

        int nightModeFlags = getContext().getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        int hintColor = ContextCompat.getColor(getContext(), R.color.transparent);

        switch (nightModeFlags) {
            case android.content.res.Configuration.UI_MODE_NIGHT_YES:
                popUpReport.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_background));
                reportTextInsert.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                reportTextInsert.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_background));
                hintColor = ContextCompat.getColor(getContext(), R.color.white);
                break;

            case android.content.res.Configuration.UI_MODE_NIGHT_NO:
                popUpReport.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_background));
                reportTextInsert.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                reportTextInsert.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_background));
                hintColor = ContextCompat.getColor(getContext(), R.color.black);
                break;

            case android.content.res.Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        reportTextInsert.setHintTextColor(hintColor);
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
        String reportText = reportTextInsert.getText().toString().trim();

        if (reportText.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, insira um motivo para o relatório.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Nenhum usuário logado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Nome não definido";
        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "Email não definido";

        Map<String, Object> report = new HashMap<>();
        report.put("reportTex", reportText);
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
        int dialogHeight = (int) (screenHeight * 0.3);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }
}