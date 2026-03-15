package com.example.goodlink.core.domain.repository;

import androidx.annotation.NonNull;
import com.example.goodlink.core.domain.model.user.User;

import java.util.Map;

public interface UserRepository {

    interface Callback<T> {
        void onSuccess(@NonNull T data);
        void onError(@NonNull String message);
    }

    void getUser(@NonNull String userId, @NonNull Callback<User> cb);

    void getUserIdToNameMap(@NonNull Callback<Map<String, String>> cb);
}