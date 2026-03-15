package com.example.goodlink.feature.forum.presentation.mapper;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.model.forum.Comment;
import com.example.goodlink.feature.forum.ui.model.CommentItemUi;

import java.util.Objects;

public final class CommentUiMapper {

    @NonNull
    public CommentItemUi toUi(@NonNull Comment c, @NonNull String currentUserId) {
        Objects.requireNonNull(c);

        return new CommentItemUi(
                c.id(),
                c.repositoryId(),
                c.userName(),
                c.text(),
                c.likesCount(),
                c.dislikesCount(),
                c.likedByMe(),
                c.dislikedByMe(),
                c.date()
        );
    }
}