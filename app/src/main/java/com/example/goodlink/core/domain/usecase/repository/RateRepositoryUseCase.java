package com.example.goodlink.core.domain.usecase.repository;

import androidx.annotation.NonNull;
import com.example.goodlink.core.domain.model.rating.RatingValue;
import com.example.goodlink.core.domain.repository.RatingRepository;

import java.util.Objects;

public final class RateRepositoryUseCase {

    private final RatingRepository repo;

    public RateRepositoryUseCase(@NonNull RatingRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    public void execute(
            @NonNull String userId,
            @NonNull String repositoryId,
            @NonNull RatingValue value,
            @NonNull RatingRepository.Callback<Void> cb
    ) {
        repo.rateRepository(userId, repositoryId, value, cb);
    }
}