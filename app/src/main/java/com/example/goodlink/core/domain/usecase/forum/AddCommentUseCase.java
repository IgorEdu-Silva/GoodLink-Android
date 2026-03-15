package com.example.goodlink.core.domain.usecase.forum;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.repository.CommentRepository;

import java.util.Objects;

public final class AddCommentUseCase {
    private final CommentRepository repo;

    public AddCommentUseCase(@NonNull CommentRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    public void execute(@NonNull String repositoryId,
                        @NonNull String userName,
                        @NonNull String text,
                        @NonNull CommentRepository.Callback<Void> cb) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            cb.onError("Comentário não pode estar vazio!");
            return;
        }
        repo.addComment(repositoryId, userName, trimmed, cb);
    }
}