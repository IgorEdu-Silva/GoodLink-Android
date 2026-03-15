package com.example.goodlink.core.domain.repository;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.model.forum.Comment;
import com.example.goodlink.core.domain.model.forum.Reaction;

import java.util.List;

public interface CommentRepository {

    interface Callback<T> {
        void onSuccess(@NonNull T data);
        void onError(@NonNull String message);
    }

    void getByRepositoryId(
            @NonNull String repositoryId,
            @NonNull String currentUserId,
            @NonNull Callback<List<Comment>> cb
    );

    void addComment(
            @NonNull String repositoryId,
            @NonNull String userName,
            @NonNull String text,
            @NonNull Callback<Void> cb
    );

    void toggleReaction(
            @NonNull String commentId,
            @NonNull String userId,
            @NonNull Reaction reaction,
            @NonNull Callback<Void> cb
    );
}