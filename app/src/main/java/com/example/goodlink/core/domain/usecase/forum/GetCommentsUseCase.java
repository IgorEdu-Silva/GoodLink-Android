package com.example.goodlink.core.domain.usecase.forum;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.model.forum.Comment;
import com.example.goodlink.core.domain.repository.CommentRepository;

import java.util.List;
import java.util.Objects;

public final class GetCommentsUseCase {
    private final CommentRepository repo;

    public GetCommentsUseCase(@NonNull CommentRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    public void execute(
            @NonNull String repositoryId,
            @NonNull String currentUserId,
            @NonNull CommentRepository.Callback<List<Comment>> cb
    ) {
        repo.getByRepositoryId(repositoryId, currentUserId, cb);
    }
}