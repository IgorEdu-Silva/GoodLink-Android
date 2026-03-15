package com.example.goodlink.feature.forum.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.R;
import com.example.goodlink.feature.forum.ui.model.CommentItemUi;

import java.util.Objects;

public class AdapterComment extends ListAdapter<CommentItemUi, AdapterComment.ViewHolder> {

    private final CommentActionListener listener;

    public AdapterComment(@NonNull CommentActionListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_comments_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentItemUi item = getItem(position);

        holder.commentTextView.setText(item.text);
        holder.nameUserTextView.setText(item.userName);

        holder.btnWithLikeComment.setBackgroundResource(
                item.likedByMe ? R.drawable.with_like : R.drawable.without_like);
        holder.btnWithDislikeComment.setBackgroundResource(
                item.dislikedByMe ? R.drawable.with_dislike : R.drawable.without_dislike);

        holder.btnCommentReport.setOnClickListener(v -> listener.onReport(item.repositoryId));
        holder.btnWithLikeComment.setOnClickListener(v -> listener.onLike(item.id));
        holder.btnWithDislikeComment.setOnClickListener(v -> listener.onDislike(item.id));
    }

    public interface CommentActionListener {
        void onLike(@NonNull String commentId);
        void onDislike(@NonNull String commentId);
        void onReport(@NonNull String repositoryId);
    }

    private static final DiffUtil.ItemCallback<CommentItemUi> DIFF =
            new DiffUtil.ItemCallback<CommentItemUi>() {
                @Override
                public boolean areItemsTheSame(@NonNull CommentItemUi a, @NonNull CommentItemUi b) {
                    return Objects.equals(a.id, b.id);
                }

                @Override
                public boolean areContentsTheSame(@NonNull CommentItemUi a, @NonNull CommentItemUi b) {
                    return Objects.equals(a.text, b.text)
                            && Objects.equals(a.userName, b.userName)
                            && a.likes == b.likes
                            && a.dislikes == b.dislikes
                            && a.likedByMe == b.likedByMe
                            && a.dislikedByMe == b.dislikedByMe;
                }
            };

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView commentTextView;
        final TextView nameUserTextView;
        final Button btnCommentReport;
        final Button btnWithLikeComment;
        final Button btnWithDislikeComment;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.UserComment);
            nameUserTextView = itemView.findViewById(R.id.UserName);
            btnCommentReport = itemView.findViewById(R.id.btnCommentReport);
            btnWithLikeComment = itemView.findViewById(R.id.btnWithLikeComment);
            btnWithDislikeComment = itemView.findViewById(R.id.btnWithDislikeComment);
        }
    }
}