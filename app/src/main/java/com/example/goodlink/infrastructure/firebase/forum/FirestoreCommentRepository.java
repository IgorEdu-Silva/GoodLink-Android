package com.example.goodlink.infrastructure.firebase.forum;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.model.forum.Comment;
import com.example.goodlink.core.domain.model.forum.Reaction;
import com.example.goodlink.core.domain.repository.CommentRepository;
import com.example.goodlink.infrastructure.firebase.firestore.FireStoreDataManager;
import com.example.goodlink.infrastructure.firebase.forum.dto.CommentDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FirestoreCommentRepository implements CommentRepository {

    private final FireStoreDataManager data;

    public FirestoreCommentRepository(@NonNull FireStoreDataManager data) {
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public void getByRepositoryId(
            @NonNull String repositoryId,
            @NonNull String currentUserId,
            @NonNull Callback<List<Comment>> cb
    ) {
        data.getCommentsByRepositoryId(repositoryId, new FireStoreDataManager.OnCommentsLoadedListener() {
            @Override
            public void onCommentsLoaded(List<CommentDocument> docs) {
                ArrayList<Comment> mapped = new ArrayList<>();
                if (docs != null) {
                    for (CommentDocument d : docs) {
                        String id = safe(d.getCommentId());
                        String repoId = safe(d.getRepositoryId());
                        String userName = safe(d.getUserName());
                        String text = safe(d.getUserComment());

                        List<String> likedBy = d.getLikedBy();
                        List<String> dislikedBy = d.getDislikedBy();

                        int likes = likedBy == null ? 0 : likedBy.size();
                        int dislikes = dislikedBy == null ? 0 : dislikedBy.size();

                        Reaction my = null;
                        if (likedBy != null && likedBy.contains(currentUserId)) my = Reaction.LIKE;
                        else if (dislikedBy != null && dislikedBy.contains(currentUserId)) my = Reaction.DISLIKE;

                        mapped.add(new Comment(
                                id,
                                repoId,
                                userName,
                                text,
                                d.getDate(),
                                likes,
                                dislikes,
                                my
                        ));
                    }
                }
                cb.onSuccess(mapped);
            }

            @Override
            public void onCommentsLoadFailed(String errorMessage) {
                cb.onError(errorMessage == null ? "Erro desconhecido" : errorMessage);
            }
        });
    }

    private static String safe(String v) { return v == null ? "" : v; }

    @Override
    public void addComment(
            @NonNull String repositoryId,
            @NonNull String userName,
            @NonNull String text,
            @NonNull Callback<Void> cb
    ) {
        data.saveUserComment(text, repositoryId, userName, new FireStoreDataManager.OnCommentSavedListener() {
            @Override public void onCommentSaved() { cb.onSuccess(null); }
            @Override public void onCommentSaved(String commentId) { cb.onSuccess(null); }
            @Override public void onCommentSaveFailed(String errorMessage) {
                cb.onError(errorMessage == null ? "Erro desconhecido" : errorMessage);
            }
        });
    }

    @Override
    public void toggleReaction(
            @NonNull String commentId,
            @NonNull String userId,
            @NonNull Reaction reaction,
            @NonNull Callback<Void> cb
    ) {
        FireStoreDataManager.CommentReaction r =
                (reaction == Reaction.LIKE)
                        ? FireStoreDataManager.CommentReaction.LIKE
                        : FireStoreDataManager.CommentReaction.DISLIKE;

        data.updateReaction(commentId, r, userId, new FireStoreDataManager.FireStoreDataListener<Void>() {
            @Override public void onSuccess(Void ignored) { cb.onSuccess(null); }
            @Override public void onFailure(String errorMessage) {
                cb.onError(errorMessage == null ? "Erro desconhecido" : errorMessage);
            }
        });
    }
}