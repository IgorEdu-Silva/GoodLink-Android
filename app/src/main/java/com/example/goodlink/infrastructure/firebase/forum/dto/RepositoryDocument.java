package com.example.goodlink.infrastructure.firebase.forum.dto;

import com.example.goodlink.core.domain.model.forum.RepositoryItem;

public class RepositoryDocument {
    private String title;
    private String description;
    private String channelName;
    private String iframe;
    private String channelUrl;
    private String category;
    private String userId;
    private String publishedAt;

    public RepositoryDocument() {}

    public static RepositoryDocument from(RepositoryItem item) {
        RepositoryDocument doc = new RepositoryDocument();
        doc.setTitle(item.title());
        doc.setDescription(item.description());
        doc.setChannelName(item.channelName());
        doc.setIframe(item.embedUrl());
        doc.setChannelUrl(item.channelUrl());
        doc.setCategory(item.category());
        doc.setUserId(item.authorUserId());
        doc.setPublishedAt(item.publishedAt());
        return doc;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }

    public String getIframe() { return iframe; }
    public void setIframe(String iframe) { this.iframe = iframe; }

    public String getChannelUrl() { return channelUrl; }
    public void setChannelUrl(String channelUrl) { this.channelUrl = channelUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
}
