package com.example.goodlink.Screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.goodlink.FireBase.MyFirebaseMessagingService;
import com.example.goodlink.Fragments.PagerAdapterFragments;
import com.example.goodlink.Functions.NotificationHelper;
import com.example.goodlink.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.messaging.FirebaseMessaging;

public class Forum extends AppCompatActivity {
    private boolean telaAtiva = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NotificationHelper.requestNotificationPermission(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        sendTokenToMessagingService(token);
                    }
                });

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayoutInfo);

        PagerAdapterFragments pagerAdapter = new PagerAdapterFragments(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Playlists");
                            break;
                        case 1:
                            tab.setText("Formulário");
                            break;
                        case 2:
                            tab.setText("Usuários");
                            break;
                    }
                }
        ).attach();
    }

    private void sendTokenToMessagingService(String token) {
        Intent intent = new Intent(this, MyFirebaseMessagingService.class);
        intent.setAction(MyFirebaseMessagingService.ACTION_TOKEN_RECEIVED);
        intent.putExtra(MyFirebaseMessagingService.EXTRA_TOKEN, token);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        if (telaAtiva) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        telaAtiva = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        telaAtiva = false;
    }
}