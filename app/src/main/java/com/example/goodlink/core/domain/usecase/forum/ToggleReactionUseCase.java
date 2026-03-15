package com.example.goodlink.core.domain.usecase.forum;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.model.forum.Reaction;
import com.example.goodlink.core.domain.repository.CommentRepository;

import java.util.Objects;

public final class ToggleReactionUseCase {
    private final CommentRepository repo;

    public ToggleReactionUseCase(@NonNull CommentRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    public void execute(@NonNull String commentId,
                        @NonNull String userId,
                        @NonNull Reaction reaction,
                        @NonNull CommentRepository.Callback<Void> cb) {
        repo.toggleReaction(commentId, userId, reaction, cb);
    }
}