package com.example.goodlink.Screens;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goodlink.R;
import com.example.goodlink.Utils.FontSizeUtils;

public class Termos extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_termos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forumScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TextView Terms = findViewById(R.id.termsTitle);
        TextView containerTerms = findViewById(R.id.containerTerms);
        FontSizeUtils.applySpecificFontSize(Terms, FontSizeUtils.getFontSize(this));
        FontSizeUtils.applySpecificFontSize(containerTerms, FontSizeUtils.getFontSize(this));
    }
}