package com.example.goodlink.infrastructure.session.forum;

import android.content.Context;

public final class ForumNotificationState {
    private static final String PREFS = "forum_prefs";
    private static final String KEY_ENGAGEMENT_SHOWN = "engagement_shown";

    private ForumNotificationState(){}

    public static boolean wasEngagementShown(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_ENGAGEMENT_SHOWN, false);
    }

    public static void markEngagementShown(Context c) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_ENGAGEMENT_SHOWN, true)
                .apply();
    }
}
