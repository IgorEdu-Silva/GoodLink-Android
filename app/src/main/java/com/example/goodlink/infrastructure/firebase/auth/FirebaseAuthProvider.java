package com.example.goodlink.infrastructure.firebase.auth;

import com.example.goodlink.core.domain.gateway.AuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public final class FirebaseAuthProvider implements AuthProvider {
    private final FirebaseAuth auth;

    public FirebaseAuthProvider(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    public String currentUserId() {
        FirebaseUser u = auth.getCurrentUser();
        return u != null ? u.getUid() : null;
    }
}