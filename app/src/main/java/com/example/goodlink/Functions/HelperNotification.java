package com.example.goodlink.Functions;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class HelperNotification {

    private static final String CHANNEL_ID = "MY_CHANNEL_ID";
    private static final String CHANNEL_NAME = "My Channel";
    private static final String CHANNEL_DESCRIPTION = "Channel for app notifications";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final String PREF_NAME = "NotificationPref";
    private static final String KEY_PERMISSION_GRANTED = "PermissionGranted";

    public static void requestNotificationPermission(FragmentActivity activity) {
        if (isPermissionDeniedPermanently(activity)) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)) {
                    Toast.makeText(activity, "Que peninha :(", Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
                }
            } else {
                setupNotificationChannel(activity);
                savePermissionGranted(activity, true);
            }
        }
    }

    public static boolean isNotificationPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isPermissionDeniedPermanently(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_PERMISSION_GRANTED, false);
    }

    public static void savePermissionGranted(Context context, boolean granted) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_PERMISSION_GRANTED, granted);
        editor.apply();
    }

    public static void setupNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_DESCRIPTION);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendNotification(Context context, String title, String message) {
        if (!isNotificationPermissionGranted(context)) {
            requestNotificationPermission((FragmentActivity) context);
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            notificationManager.notify(1, builder.build());
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Context context) {
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupNotificationChannel(context);
                savePermissionGranted(context, true);
            } else {
                savePermissionGranted(context, false);
                Toast.makeText(context, "Que peninha :(", Toast.LENGTH_LONG).show();
            }
        }
    }
}
