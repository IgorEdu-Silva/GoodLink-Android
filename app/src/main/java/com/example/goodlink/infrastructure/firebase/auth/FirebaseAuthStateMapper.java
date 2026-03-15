package com.example.goodlink.infrastructure.firebase.auth;

import com.example.goodlink.core.domain.auth.AuthState;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthStateMapper {
    private FirebaseAuthStateMapper() {}

    public static AuthState from(FirebaseUser user) {
        if (user == null) return AuthState.SIGNED_OUT;
        return user.isAnonymous() ? AuthState.ANONYMOUS : AuthState.AUTHENTICATED;
    }
}
