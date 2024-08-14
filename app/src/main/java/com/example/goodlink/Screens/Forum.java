package com.example.goodlink.Screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.goodlink.Functions.MessagingService;
import com.example.goodlink.Adapter.AdapterPagerFragments;
import com.example.goodlink.Functions.HelperNotification;
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

        HelperNotification.requestNotificationPermission(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        sendTokenToMessagingService(token);
                    }
                });

        ViewPager2 viewPager = findViewById(R.id.RepositoriesView);
        TabLayout tabLayout = findViewById(R.id.tabLayoutInfo);

        AdapterPagerFragments pagerAdapter = new AdapterPagerFragments(this);
        viewPager.setAdapter(pagerAdapter);

        sendNotificationToRepositoryOwner();

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Repositórios");
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
        Intent intent = new Intent(this, MessagingService.class);
        intent.setAction(MessagingService.ACTION_TOKEN_RECEIVED);
        intent.putExtra(MessagingService.EXTRA_TOKEN, token);
        startService(intent);
    }

    private void sendNotificationToRepositoryOwner() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        int uniqueNotificationId = MessagingService.generateUniqueNotificationId();
                        MessagingService.sendNotificationToToken(this, token, uniqueNotificationId, "Gostou do que viu?", "Ajude mais pessoas da comunidade e compartilhe um pouco do seu conhecimento!");
                    }
                });
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