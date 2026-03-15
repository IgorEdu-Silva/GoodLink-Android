package com.example.goodlink.infrastructure.fcm;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessaging;

public final class FcmTokenProvider {

    public interface Callback {
        void onSuccess(String token);
        void onFailure(Exception e);
    }

    private final Context appContext;
    private final FCMMessagingService fcmService;

    public FcmTokenProvider(Context context, FCMMessagingService fcmService) {
        this.appContext = context.getApplicationContext();
        this.fcmService = fcmService;
    }

    public void fetchAndPersist(Callback cb) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult();
                fcmService.saveTokenToPrefs(appContext, token);
                cb.onSuccess(token);
            } else {
                cb.onFailure(task.getException());
            }
        });
    }
}
