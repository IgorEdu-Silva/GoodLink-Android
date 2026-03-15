package com.example.goodlink.infrastructure.firebase.forum.mapper;

import com.example.goodlink.core.domain.model.forum.RepositoryItem;
import com.example.goodlink.infrastructure.firebase.forum.dto.RepositoryDocument;

import java.util.Objects;

public final class RepositoryMapper {
    private RepositoryMapper() {}

    public static RepositoryItem toDomain(
            String docId,
            RepositoryDocument d,
            boolean favoritedByMe
    ) {
        Objects.requireNonNull(docId);
        Objects.requireNonNull(d);

        return new RepositoryItem(
                safe(docId),
                safe(d.getTitle()),
                safe(d.getDescription()),
                safe(d.getChannelName()),
                safe(d.getIframe()),
                safe(d.getChannelUrl()),
                safe(d.getCategory()),
                safe(d.getUserId()),
                d.getPublishedAt(),
                favoritedByMe
        );
    }

    public static RepositoryDocument toDocument(RepositoryItem item) {
        Objects.requireNonNull(item);

        RepositoryDocument d = new RepositoryDocument();
        d.setTitle(item.title());
        d.setDescription(item.description());
        d.setChannelName(item.channelName());
        d.setIframe(item.embedUrl());
        d.setChannelUrl(item.channelUrl());
        d.setCategory(item.category());
        d.setUserId(item.authorUserId());
        d.setPublishedAt(item.publishedAt());
        return d;
    }

    private static String safe(String v) { return v == null ? "" : v; }
}