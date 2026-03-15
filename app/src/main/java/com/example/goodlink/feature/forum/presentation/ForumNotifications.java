package com.example.goodlink.feature.forum.presentation;

import android.app.Activity;
import android.app.NotificationManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.goodlink.infrastructure.fcm.FCMMessagingService;
import com.example.goodlink.infrastructure.fcm.FcmMessagingLocal;
import com.example.goodlink.infrastructure.fcm.FcmTokenProvider;
import com.example.goodlink.infrastructure.notification.HelperNotification;
import com.example.goodlink.infrastructure.session.forum.ForumNotificationState;

public final class ForumNotifications {

    private final FragmentActivity activity;
    private final FcmTokenProvider tokenProvider;

    public ForumNotifications(FragmentActivity  activity, FcmTokenProvider tokenProvider) {
        this.activity = activity;
        this.tokenProvider = tokenProvider;
    }

    public void setupTokenService() {
        HelperNotification.requestNotificationPermission(activity);

        tokenProvider.fetchAndPersist(new FcmTokenProvider.Callback() {
            @Override public void onSuccess(String token) { }
            @Override public void onFailure(Exception e) {
                Log.e("ForumNotifications", "Falha ao obter token FCM", e);
            }
        });
    }

    public void maybeSendEngagementNotification() {
        HelperNotification.requestNotificationPermission(activity);

        if (!HelperNotification.areNotificationsAllowed(activity)) return;
        if (ForumNotificationState.wasEngagementShown(activity)) return;
        if (isNotificationAlreadyActive()) return;

        FcmMessagingLocal.showEngagement(activity);
        ForumNotificationState.markEngagementShown(activity);
    }

    private boolean isNotificationAlreadyActive() {
        NotificationManager nm =
                (NotificationManager) activity.getSystemService(Activity.NOTIFICATION_SERVICE);
        if (nm == null) return false;

        StatusBarNotification[] notifications = nm.getActiveNotifications();
        for (StatusBarNotification n : notifications) {
            if (n.getId() == FCMMessagingService.NOTIFICATION_ID) return true;
        }
        return false;
    }
}
