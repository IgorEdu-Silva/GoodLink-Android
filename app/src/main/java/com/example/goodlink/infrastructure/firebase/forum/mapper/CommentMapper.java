package com.example.goodlink.infrastructure.firebase.forum.mapper;

import com.example.goodlink.core.domain.model.forum.Comment;
import com.example.goodlink.core.domain.model.forum.Reaction;
import com.example.goodlink.infrastructure.firebase.forum.dto.CommentDocument;

import java.util.ArrayList;
import java.util.List;

public final class CommentMapper {
    private CommentMapper() {}

    public static Comment toDomain(CommentDocument doc, String currentUserId) {
        List<String> likedBy = doc.getLikedBy();
        List<String> dislikedBy = doc.getDislikedBy();

        Reaction my = null;
        if (likedBy != null && likedBy.contains(currentUserId)) my = Reaction.LIKE;
        else if (dislikedBy != null && dislikedBy.contains(currentUserId)) my = Reaction.DISLIKE;

        int likes = likedBy == null ? 0 : likedBy.size();
        int dislikes = dislikedBy == null ? 0 : dislikedBy.size();

        return new Comment(
                safe(doc.getCommentId()),
                safe(doc.getRepositoryId()),
                safe(doc.getUserName()),
                safe(doc.getUserComment()),
                doc.getDate(),
                likes,
                dislikes,
                my
        );
    }

    public static CommentDocument toDocument(String commentId, String repositoryId, String userName, String text, String date) {
        CommentDocument d = new CommentDocument();
        d.setCommentId(commentId);
        d.setRepositoryId(repositoryId);
        d.setUserName(userName);
        d.setUserComment(text);
        d.setDate(date);
        d.setLikedBy(new ArrayList<>());
        d.setDislikedBy(new ArrayList<>());
        return d;
    }

    private static String safe(String v) { return v == null ? "" : v; }
    private static List<String> nonNullList(List<String> v) { return v == null ? new ArrayList<>() : v; }
}