package com.example.goodlink.PopUp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.goodlink.R;

public class PopUpCreateRepository extends Fragment {

    private final Context context;
    private PopupWindow popupWindow;
    private View popupView;
    private EditText editTextRepositoryName;
    private Button buttonSave;
    private Button buttonCancel;

    public PopUpCreateRepository(Context context) {
        this.context = context;
        initPopup();
    }

    private void initPopup() {
        ViewGroup parent = (ViewGroup) ((Activity) context).getWindow().getDecorView().getRootView();
        popupView = LayoutInflater.from(context).inflate(R.layout.fragment_pop_up_create_repository, parent, false);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        editTextRepositoryName = popupView.findViewById(R.id.inputNameRepository);
        buttonSave = popupView.findViewById(R.id.btnSave);
        buttonCancel = popupView.findViewById(R.id.btnCancel);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String repositoryName = editTextRepositoryName.getText().toString().trim();
                if (!repositoryName.isEmpty()) {
                    dismiss();
                    Toast.makeText(context, "Repositório salva: " + repositoryName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Digite um nome para o repositório", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show(View anchorView) {
        Log.d(TAG, "Attempting to show PopupWindow");
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAsDropDown(anchorView, 0, 0);
            Log.d(TAG, "PopupWindow shown");
        } else {
            Log.d(TAG, "PopupWindow not shown, either null or already showing");
        }
    }

    public void dismiss() {
        popupWindow.dismiss();
    }
}