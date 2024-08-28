package com.example.goodlink.Adapter;

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
import com.example.goodlink.PopUp.PopUpReport;
import com.example.goodlink.R;

import java.util.List;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder> {
    private final FragmentActivity activity;
    private final List<ManagerComment> commentsList;
    private final String repositoryId;
    private final String userName;

    public AdapterComment(FragmentActivity activity, List<ManagerComment> commentsList, String repositoryId, String userName) {
        this.activity = activity;
        this.commentsList = commentsList;
        this.repositoryId = repositoryId;
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

        holder.btnCommentReport.setOnClickListener(v -> {
            String repositoryId = comment.getRepositoryId();
            showReportPopup(repositoryId);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.UserComment);
            nameUserTextView = itemView.findViewById(R.id.UserName);
            btnCommentReport = itemView.findViewById(R.id.btnCommentReport);
        }
    }

    private void showReportPopup(String repositoryId) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        PopUpReport popUpReport = PopUpReport.newInstance(repositoryId);
        popUpReport.show(fragmentManager, "popup_report");
    }

}
