package com.example.goodlink.infrastructure.firebase.rating;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.model.rating.RatingValue;
import com.example.goodlink.core.domain.repository.RatingRepository;
import com.example.goodlink.infrastructure.firebase.firestore.FireStoreDataManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class FirestoreRatingRepository implements RatingRepository {

    private final FireStoreDataManager data;

    public FirestoreRatingRepository(@NonNull FireStoreDataManager data) {
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public void rateRepository(@NonNull String userId,
                               @NonNull String repositoryId,
                               @NonNull RatingValue value,
                               @NonNull Callback<Void> cb) {

        // Reaproveita sua estrutura atual:
        // users/{userId}/userRatings/{repositoryId}
        data.saveRepositoryRating(userId, repositoryId, String.valueOf(value.score()),
                new FireStoreDataManager.OnRepositoryRatingSavedListener() {
                    @Override public void onRepositoryRatingSaved(String ignored) { cb.onSuccess(null); }
                    @Override public void onRepositoryRatingSaveFailed(String errorMessage) {
                        cb.onError(errorMessage == null ? "Erro desconhecido" : errorMessage);
                    }
                });
    }
}