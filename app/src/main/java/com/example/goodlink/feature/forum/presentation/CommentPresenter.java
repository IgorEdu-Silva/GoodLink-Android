package com.example.goodlink.feature.forum.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.goodlink.core.domain.model.forum.Comment;
import com.example.goodlink.core.domain.model.forum.Reaction;
import com.example.goodlink.core.domain.repository.CommentRepository;
import com.example.goodlink.core.domain.usecase.forum.AddCommentUseCase;
import com.example.goodlink.core.domain.usecase.forum.GetCommentsUseCase;
import com.example.goodlink.core.domain.usecase.forum.ToggleReactionUseCase;
import com.example.goodlink.infrastructure.firebase.firestore.FireStoreDataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CommentPresenter {

    public interface View {
        void render(@NonNull List<Comment> comments);
        void showMessage(@NonNull String message);
        void showError(@NonNull String message);
    }

    private final GetCommentsUseCase getComments;
    private final AddCommentUseCase addComment;

    // Mantém estado local
    private final ArrayList<Comment> current = new ArrayList<>();

    private final ToggleReactionUseCase toggleReaction;
    private final String repositoryId;
    private final String currentUserId;
    private final String userName;
    private final View view;

    public CommentPresenter(
            @NonNull FireStoreDataManager data,
            @NonNull CommentRepository repo,
            @NonNull String repositoryId,
            @NonNull String currentUserId,
            @Nullable String userName,
            @NonNull View view
    ) {
        this.getComments = new GetCommentsUseCase(Objects.requireNonNull(repo));
        this.addComment = new AddCommentUseCase(Objects.requireNonNull(repo));

        this.repositoryId = Objects.requireNonNull(repositoryId);
        this.currentUserId = Objects.requireNonNull(currentUserId);
        this.userName = userName;
        this.view = Objects.requireNonNull(view);
        toggleReaction = new ToggleReactionUseCase(repo);
    }

    public void load() {
        getComments.execute(repositoryId, currentUserId, new CommentRepository.Callback<List<Comment>>() {
            @Override
            public void onSuccess(@NonNull List<Comment> data) {
                current.clear();
                current.addAll(data);
                view.render(Collections.unmodifiableList(new ArrayList<>(current)));
            }

            @Override
            public void onError(@NonNull String message) {
                view.showError("Erro ao carregar comentários: " + message);
            }
        });
    }

    public void sendComment(@NonNull String text) {
        if (userName == null || userName.trim().isEmpty()) {
            view.showError("Nome do usuário indisponível.");
            return;
        }

        addComment.execute(repositoryId, userName, text, new CommentRepository.Callback<Void>() {
            @Override
            public void onSuccess(@NonNull Void ignored) {
                view.showMessage("Comentário enviado com sucesso!");
                load();
            }

            @Override
            public void onError(@NonNull String message) {
                view.showError("Erro ao enviar comentário: " + message);
            }
        });
    }

    public void toggleLike(@NonNull String commentId) {
        toggleReaction(commentId, Reaction.LIKE);
    }

    public void toggleDislike(@NonNull String commentId) {
        toggleReaction(commentId, Reaction.DISLIKE);
    }

    private void toggleReaction(@NonNull String commentId, @NonNull Reaction reaction) {
        int idx = indexOf(commentId);
        if (idx < 0) return;

        Comment old = current.get(idx);
        Comment updated = old.toggleReaction(reaction);

        // Atualiza estado local e UI imediatamente (snappy)
        current.set(idx, updated);
        view.render(Collections.unmodifiableList(new ArrayList<>(current)));

        // Persiste usando seu método atual (listas prontas)
        toggleReaction.execute(commentId, currentUserId, reaction, new CommentRepository.Callback<Void>() {
            @Override public void onSuccess(@NonNull Void ignored) {
                // opcional: nada
            }

            @Override public void onError(@NonNull String message) {
                view.showError("Erro ao atualizar reação: " + message);
                load(); // rollback por recarga
            }
        });
    }

    public void sortByLikesDesc() {
        Collections.sort(current, (a, b) -> Integer.compare(b.likesCount(), a.likesCount()));
        view.render(Collections.unmodifiableList(new ArrayList<>(current)));
    }

    public void sortByDislikesDesc() {
        Collections.sort(current, (a, b) -> Integer.compare(b.dislikesCount(), a.dislikesCount()));
        view.render(Collections.unmodifiableList(new ArrayList<>(current)));
    }

    private int indexOf(@NonNull String commentId) {
        for (int i = 0; i < current.size(); i++) {
            if (commentId.equals(current.get(i).id())) return i;
        }
        return -1;
    }
}