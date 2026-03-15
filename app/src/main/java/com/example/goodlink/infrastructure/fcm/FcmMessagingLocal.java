package com.example.goodlink.infrastructure.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.goodlink.R;
import com.example.goodlink.feature.forum.ui.ForumActivity;

public final class FcmMessagingLocal {

    public static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "default_notification_channel_id";
    private static final String CHANNEL_NAME = "GoodLink";

    private FcmMessagingLocal(){}

    public static void showEngagement(Context context) {
        show(context,
                "Gostou do que viu?",
                "Ajude mais pessoas da comunidade e compartilhe um pouco do seu conhecimento!");
    }

    public static void show(Context context, String title, String messageBody) {
        Intent intent = new Intent(context, ForumActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_goodlink)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            );
            nm.createNotificationChannel(channel);
        }

        nm.notify(NOTIFICATION_ID, builder.build());
    }
}
