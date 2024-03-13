package com.example.goodlink;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterAndLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_and_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAndLogin.this, LoginAndRegister.class);
                startActivity(intent);
            }
        });

        CheckBox checkBoxServices = findViewById(R.id.checkBoxServices);
        String checkBoxText = checkBoxServices.getText().toString();
        SpannableString spannableStringTermos = getSpannableStringTermos(checkBoxText);
        checkBoxServices.setText(spannableStringTermos);
        checkBoxServices.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @NonNull
    private SpannableString getSpannableStringTermos(String checkBoxText) {
        int startIndex = checkBoxText.indexOf("termos e politica");
        int endIndex = startIndex + "termos e politica".length();

        SpannableString spannableString = new SpannableString(checkBoxText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xFF0099DD);
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                openPageTermos();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public void openPageTermos() {
        Intent intent = new Intent(this, Termos.class);
        startActivity(intent);
    }


}