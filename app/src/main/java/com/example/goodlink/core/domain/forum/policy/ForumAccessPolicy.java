package com.example.goodlink.core.domain.forum.policy;

import com.example.goodlink.core.domain.auth.AuthState;

public final class ForumAccessPolicy {
    private ForumAccessPolicy() {}

    public static boolean canUseFab(AuthState auth) {
        return auth == null || !auth.isAnonymous();
    }

    public static boolean canSwipePages(AuthState auth) {
        return auth == null || !auth.isAnonymous();
    }

    public static boolean canAccessTab(int position, AuthState auth) {
        if (position == 1) return auth == null || !auth.isAnonymous();
        return true;
    }
}
