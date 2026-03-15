package com.example.goodlink.feature.forum.ui;

import android.os.Bundle;
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

import com.example.goodlink.core.domain.auth.AuthState;
import com.example.goodlink.feature.forum.presentation.ForumNotifications;
import com.example.goodlink.feature.user.ui.dialog.FragmentDialogForm;
import com.example.goodlink.infrastructure.fcm.FCMMessagingService;
import com.example.goodlink.infrastructure.fcm.FcmTokenProvider;
import com.example.goodlink.infrastructure.firebase.auth.FirebaseAuthStateMapper;
import com.example.goodlink.infrastructure.lifecycle.HelperForumLifecycleObserver;
import com.example.goodlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForumActivity extends AppCompatActivity implements ForumUiBinder.UiActions {
    private ForumUiBinder uiBinder;
    private HelperForumLifecycleObserver lifecycleObserver;
    private ForumNotifications forumNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum);

        lifecycleObserver = new HelperForumLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);

        uiBinder = new ForumUiBinder(this, this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthState authState = FirebaseAuthStateMapper.from(user);
        uiBinder.bind(authState);

        forumNotifications = new ForumNotifications(this, new FcmTokenProvider(this, new FCMMessagingService()));
        forumNotifications.setupTokenService();
        forumNotifications.maybeSendEngagementNotification();
        setupInsets();
    }

    @Override
    public void openFormDialog() {
        FragmentDialogForm dialog = new FragmentDialogForm();
        dialog.show(getSupportFragmentManager(), "FormDialog");
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forumScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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