package com.example.goodlink.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.FireBaseManager.ManagerComment;
import com.example.goodlink.R;

import java.util.List;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder> {
    private final List<ManagerComment> commentsList;
    private final String playlistId;
    private final String userName;

    public AdapterComment(List<ManagerComment> commentsList, String playlistId, String userName) {
        this.commentsList = commentsList;
        this.playlistId = playlistId;
        this.userName = userName;
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
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        TextView nameUserTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.UserComment);
            nameUserTextView = itemView.findViewById(R.id.UserName);
        }
    }
}
