package com.example.goodlink.core.domain.gateway;

import androidx.annotation.Nullable;

public interface AuthProvider {
    @Nullable String currentUserId();
}