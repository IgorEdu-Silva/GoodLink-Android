package com.example.goodlink.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.FireBaseManager.ManagerComment;
import com.example.goodlink.PopUps.PopUpReport;
import com.example.goodlink.R;

import java.util.List;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder> {
    private final FragmentActivity activity;
    private final List<ManagerComment> commentsList;
    private final String repositoryId;
    private final String userName;
    private final String currentUserId;
    private final CommentActionListener commentActionListener;

    public AdapterComment(FragmentActivity activity, List<ManagerComment> commentsList, String repositoryId, String userName, String currentUserId, CommentActionListener commentActionListener) {
        this.activity = activity;
        this.commentsList = commentsList;
        this.repositoryId = repositoryId;
        this.userName = userName;
        this.currentUserId = currentUserId;
        this.commentActionListener = commentActionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_comments_activity, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int reversePosition = getItemCount() - 1 - position;
        ManagerComment comment = commentsList.get(reversePosition);

        holder.commentTextView.setText(comment.getUserComment());
        holder.nameUserTextView.setText(comment.getUserName());

        updateLikeDislikeButtons(holder, comment);

        holder.btnCommentReport.setOnClickListener(v -> {
            String repositoryId = comment.getRepositoryId();
            showReportPopup(repositoryId);
        });

        updateLikeDislikeButtons(holder, comment);

        holder.btnWithLikeComment.setOnClickListener(v -> {
            if (comment.isLikedByUser(currentUserId)) {
                holder.btnWithLikeComment.setBackgroundResource(R.drawable.without_like);
                comment.removeLike(currentUserId);
                commentActionListener.onLikeClicked(comment);
            } else {
                holder.btnWithLikeComment.setBackgroundResource(R.drawable.with_like);
                holder.btnWithDislikeComment.setBackgroundResource(R.drawable.without_dislike);
                comment.addLike(currentUserId);
                comment.removeDislike(currentUserId);
                commentActionListener.onLikeClicked(comment);
            }
            updateLikeDislikeButtons(holder, comment);
        });


        holder.btnWithDislikeComment.setOnClickListener(v -> {
            if (comment.isDislikedByUser(currentUserId)) {
                holder.btnWithDislikeComment.setBackgroundResource(R.drawable.without_dislike);
                comment.removeDislike(currentUserId);
                commentActionListener.onDislikeClicked(comment);
            } else {
                holder.btnWithDislikeComment.setBackgroundResource(R.drawable.with_dislike);
                holder.btnWithLikeComment.setBackgroundResource(R.drawable.without_like);
                comment.addDislike(currentUserId);
                comment.removeLike(currentUserId);
                commentActionListener.onDislikeClicked(comment);
            }
            updateLikeDislikeButtons(holder, comment);
        });

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        TextView nameUserTextView;
        Button btnCommentReport;
        Button btnWithLikeComment;
        Button btnWithDislikeComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.UserComment);
            nameUserTextView = itemView.findViewById(R.id.UserName);
            btnCommentReport = itemView.findViewById(R.id.btnCommentReport);
            btnWithLikeComment = itemView.findViewById(R.id.btnWithLikeComment);
            btnWithDislikeComment = itemView.findViewById(R.id.btnWithDislikeComment);
        }
    }

    private void updateLikeDislikeButtons(ViewHolder holder, ManagerComment comment) {
        boolean isLiked = comment.isLikedByUser(currentUserId);
        boolean isDisliked = comment.isDislikedByUser(currentUserId);

        Log.d("AdapterComment", "Updating buttons: Liked = " + isLiked + ", Disliked = " + isDisliked);
        Log.d("PopUpComment", "Updating comment: " + comment.getCommentId());

        holder.btnWithLikeComment.setBackgroundResource(isLiked ? R.drawable.with_like : R.drawable.without_like);
        holder.btnWithDislikeComment.setBackgroundResource(isDisliked ? R.drawable.with_dislike : R.drawable.without_dislike);
    }

    private void showReportPopup(String repositoryId) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        PopUpReport popUpReport = PopUpReport.newInstance(repositoryId);
        popUpReport.show(fragmentManager, "popup_report");
    }

    public interface CommentActionListener {
        void onLikeClicked(ManagerComment comment);
        void onDislikeClicked(ManagerComment comment);
    }

}
