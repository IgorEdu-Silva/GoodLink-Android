package com.example.goodlink.Screens;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.example.goodlink.Fragments.FragmentDialogForm;
import com.example.goodlink.FCM.FCMMessagingService;
import com.example.goodlink.Adapter.AdapterPagerFragments;
import com.example.goodlink.Functions.HelperForumLifecycleObserver;
import com.example.goodlink.Functions.HelperNotification;
import com.example.goodlink.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class Forum extends AppCompatActivity {
    private final boolean telaAtiva = true;
    private FloatingActionButton fab;
    private HelperForumLifecycleObserver lifecycleObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum);

        lifecycleObserver = new HelperForumLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);

        iniUI();
        setupToken();
    }

    private void iniUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ViewPager2 viewPager = findViewById(R.id.RepositoriesView);
        TabLayout tabLayout = findViewById(R.id.tabLayoutInfo);
        fab = findViewById(R.id.FloatingBtnPageCentral);
        fab.setEnabled(user == null || !user.isAnonymous());

        AdapterPagerFragments pagerAdapter = new AdapterPagerFragments(this);
        viewPager.setAdapter(pagerAdapter);

        sendNotificationToRepositoryOwner();

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setIcon(R.drawable.btn_home);
                            break;
                        case 1:
                            tab.setIcon(R.drawable.btn_settings);
                            break;
                    }
                }
        ).attach();

        fab.setOnClickListener(view -> openFormDialog());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageSelected(int position){
                super.onPageSelected(position);
                if (position == 1) {
                    fab.setVisibility(View.GONE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forumScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupToken() {
        try {
            HelperNotification.requestNotificationPermission(this);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String token = task.getResult();
                            sendTokenToMessagingService(token);
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendTokenToMessagingService(String token) {
        Intent intent = new Intent(this, FCMMessagingService.class);
        intent.setAction(FCMMessagingService.ACTION_TOKEN_RECEIVED);
        intent.putExtra(FCMMessagingService.EXTRA_TOKEN, token);
        startService(intent);
    }

    private void sendNotificationToRepositoryOwner() {
        if (isNotificationAlreadyActive()) {
            return;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        FCMMessagingService.sendNotificationToToken(this, token, "Gostou do que viu?", "Ajude mais pessoas da comunidade e compartilhe um pouco do seu conhecimento!");
                    }
                });
    }

    private boolean isNotificationAlreadyActive() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {
                if (notification.getId() == FCMMessagingService.NOTIFICATION_ID) {
                    return true;
                }
            }
        }
        return false;
    }

    private void openFormDialog() {
        FragmentDialogForm dialog = new FragmentDialogForm();
        dialog.show(getSupportFragmentManager(), "FormDialog");
    }

    public static class FormDialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_page_form, container, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(lifecycleObserver);
    }
}