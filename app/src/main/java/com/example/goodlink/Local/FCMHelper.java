package com.example.goodlink.Local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class FCMHelper extends FirebaseMessagingService {
    private static final String TAG = "FCMHelper";
    public static final String FCM_PREFS = "FCM_PREFS";
    public static final String FCM_TOKEN_KEY = "FCM_TOKEN";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        saveTokenToPrefs(token);
    }

    private void saveTokenToPrefs(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(FCM_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FCM_TOKEN_KEY, token);
        editor.apply();
    }

    public static String getFCMToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FCM_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FCM_TOKEN_KEY, null);
    }
}
