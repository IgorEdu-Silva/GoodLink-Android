package com.example.goodlink.core.domain.model.forum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public final class Comment {
    private final String id;
    private final String repositoryId;
    private final String userName;
    private final String text;
    private final String date;

    private final int likesCount;
    private final int dislikesCount;
    @Nullable private final Reaction myReaction;

    public Comment(
            @NonNull String id,
            @NonNull String repositoryId,
            @NonNull String userName,
            @NonNull String text,
            @Nullable String date,
            int likesCount,
            int dislikesCount,
            @Nullable Reaction myReaction
    ) {
        this.id = Objects.requireNonNull(id);
        this.repositoryId = Objects.requireNonNull(repositoryId);
        this.userName = Objects.requireNonNull(userName);
        this.text = Objects.requireNonNull(text);
        this.date = date;
        this.likesCount = Math.max(0, likesCount);
        this.dislikesCount = Math.max(0, dislikesCount);
        this.myReaction = myReaction;
    }

    @NonNull public String id() { return id; }
    @NonNull public String repositoryId() { return repositoryId; }
    @NonNull public String userName() { return userName; }
    @NonNull public String text() { return text; }
    @Nullable public String date() { return date; }

    public int likesCount() { return likesCount; }
    public int dislikesCount() { return dislikesCount; }
    public boolean likedByMe() { return myReaction == Reaction.LIKE; }
    public boolean dislikedByMe() { return myReaction == Reaction.DISLIKE; }
    @Nullable public Reaction myReaction() { return myReaction; }

    @NonNull
    public Comment toggleReaction(@NonNull Reaction reaction) {
        Objects.requireNonNull(reaction);

        int newLikes = likesCount;
        int newDislikes = dislikesCount;
        Reaction newMy = myReaction;

        if (reaction == Reaction.LIKE) {
            if (myReaction == Reaction.LIKE) {
                newLikes = Math.max(0, newLikes - 1);
                newMy = null;
            } else {
                newLikes++;
                if (myReaction == Reaction.DISLIKE) newDislikes = Math.max(0, newDislikes - 1);
                newMy = Reaction.LIKE;
            }
        } else {
            if (myReaction == Reaction.DISLIKE) {
                newDislikes = Math.max(0, newDislikes - 1);
                newMy = null;
            } else {
                newDislikes++;
                if (myReaction == Reaction.LIKE) newLikes = Math.max(0, newLikes - 1);
                newMy = Reaction.DISLIKE;
            }
        }

        return new Comment(id, repositoryId, userName, text, date, newLikes, newDislikes, newMy);
    }
}