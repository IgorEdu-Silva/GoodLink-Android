package com.example.goodlink.feature.forum.ui.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public final class CommentItemUi {
    public final String id;
    public final String repositoryId;
    public final String userName;
    public final String text;
    public final int likes;
    public final int dislikes;
    public final boolean likedByMe;
    public final boolean dislikedByMe;
    @Nullable public final String date;

    public CommentItemUi(
            @NonNull String id,
            @NonNull String repositoryId,
            @NonNull String userName,
            @NonNull String text,
            int likes,
            int dislikes,
            boolean likedByMe,
            boolean dislikedByMe,
            @Nullable String date
    ) {
        this.id = Objects.requireNonNull(id);
        this.repositoryId = Objects.requireNonNull(repositoryId);
        this.userName = Objects.requireNonNull(userName);
        this.text = Objects.requireNonNull(text);
        this.likes = likes;
        this.dislikes = dislikes;
        this.likedByMe = likedByMe;
        this.dislikedByMe = dislikedByMe;
        this.date = date;
    }
}