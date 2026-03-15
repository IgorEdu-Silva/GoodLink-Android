package com.example.goodlink.core.domain.auth;

public enum AuthState {
    ANONYMOUS,
    AUTHENTICATED,
    SIGNED_OUT;

    public boolean isAnonymous() {
        return this == ANONYMOUS;
    }
}
