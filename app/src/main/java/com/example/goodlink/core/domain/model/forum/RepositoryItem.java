package com.example.goodlink.core.domain.model.forum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public final class RepositoryItem {

    private final String id;
    private final String title;
    private final String description;
    private final String channelName;
    private final String embedUrl;
    private final String channelUrl;
    private final String category;
    private final String authorUserId;
    private final String publishedAt;
    private final boolean favoritedByMe;

    public RepositoryItem(
            @NonNull String id,
            @NonNull String title,
            @NonNull String description,
            @NonNull String channelName,
            @NonNull String embedUrl,
            @NonNull String channelUrl,
            @NonNull String category,
            @NonNull String authorUserId,
            @Nullable String publishedAt,
            boolean favoritedByMe
    ) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.channelName = Objects.requireNonNull(channelName);
        this.embedUrl = Objects.requireNonNull(embedUrl);
        this.channelUrl = Objects.requireNonNull(channelUrl);
        this.category = Objects.requireNonNull(category);
        this.authorUserId = Objects.requireNonNull(authorUserId);
        this.publishedAt = publishedAt;
        this.favoritedByMe = favoritedByMe;
    }

    @NonNull public String id() { return id; }
    @NonNull public String title() { return title; }
    @NonNull public String description() { return description; }
    @NonNull public String channelName() { return channelName; }
    @NonNull public String embedUrl() { return embedUrl; }
    @NonNull public String channelUrl() { return channelUrl; }
    @NonNull public String category() { return category; }
    @NonNull public String authorUserId() { return authorUserId; }
    @Nullable public String publishedAt() { return publishedAt; }

    public boolean favoritedByMe() { return favoritedByMe; }

    public RepositoryItem withFavoritedByMe(boolean v) {
        return new RepositoryItem(id, title, description, channelName, embedUrl, channelUrl, category, authorUserId, publishedAt, v);
    }
}