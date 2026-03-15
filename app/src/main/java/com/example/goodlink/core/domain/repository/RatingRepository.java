package com.example.goodlink.core.domain.repository;

import androidx.annotation.NonNull;
import com.example.goodlink.core.domain.model.rating.RatingValue;

public interface RatingRepository {

    interface Callback<T> {
        void onSuccess(@NonNull T data);
        void onError(@NonNull String message);
    }

    void rateRepository(
            @NonNull String userId,
            @NonNull String repositoryId,
            @NonNull RatingValue value,
            @NonNull Callback<Void> cb
    );
}