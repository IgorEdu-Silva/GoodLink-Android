package com.example.goodlink.core.domain.repository;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.model.forum.RepositoryItem;

import java.util.List;

public interface RepositoryFeedRepository {
    interface Callback<T> {
        void onSuccess(@NonNull T data);
        void onError(@NonNull String message);
    }

    void getAll(
            @NonNull String currentUserId,
            @NonNull Callback<List<RepositoryItem>> cb
    );

    void toggleFavorite(
            @NonNull String currentUserId,
            @NonNull String repositoryId,
            @NonNull Callback<Boolean> cb
    );

    void saveRating(
            @NonNull String currentUserId,
            @NonNull String repositoryId,
            @NonNull String rating,
            @NonNull Callback<Void> cb
    );
}
