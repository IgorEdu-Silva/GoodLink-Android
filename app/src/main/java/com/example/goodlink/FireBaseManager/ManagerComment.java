package com.example.goodlink.FireBaseManager;

import java.util.ArrayList;
import java.util.List;

public class ManagerComment {
    private String userName;
    private String userComment;
    private String repositoryId;
    private String commentId;
    private List<String> likedBy;
    private List<String> dislikedBy;
    private String date;

    public ManagerComment() {
        this.likedBy = new ArrayList<>();
        this.dislikedBy = new ArrayList<>();
    }

    public ManagerComment(String userName, String userComment, String repositoryId, String commentId, String date) {
        this.userName = userName;
        this.userComment = userComment;
        this.repositoryId = repositoryId;
        this.commentId = commentId;
        this.date = date;
    }

    public ManagerComment(String commentId, String userComment, String userName, String repositoryId, List<String> likedBy, List<String> dislikedBy) {
        this.commentId = commentId;
        this.userComment = userComment;
        this.userName = userName;
        this.repositoryId = repositoryId;
        this.likedBy = likedBy != null ? likedBy : new ArrayList<>();
        this.dislikedBy = dislikedBy != null ? dislikedBy : new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy != null ? likedBy : new ArrayList<>();
    }

    public List<String> getDislikedBy() {
        return dislikedBy;
    }

    public void setDislikedBy(List<String> dislikedBy) {
        this.dislikedBy = dislikedBy != null ? dislikedBy : new ArrayList<>();
    }

    public boolean isLikedByUser(String userId) {
        return likedBy != null && likedBy.contains(userId);
    }

    public boolean isDislikedByUser(String userId) {
        return dislikedBy != null && dislikedBy.contains(userId);
    }

    public void addLike(String userId) {
        if (likedBy == null) likedBy = new ArrayList<>();
        if (!likedBy.contains(userId)) likedBy.add(userId);
    }

    public void removeLike(String userId) {
        if (likedBy != null) likedBy.remove(userId);
    }

    public void addDislike(String userId) {
        if (dislikedBy == null) dislikedBy = new ArrayList<>();
        if (!dislikedBy.contains(userId)) dislikedBy.add(userId);
    }

    public void removeDislike(String userId) {
        if (dislikedBy != null) dislikedBy.remove(userId);
    }

    public int getLikesCount() {
        return likedBy != null ? likedBy.size() : 0;
    }

    public int getDislikesCount() {
        return dislikedBy != null ? dislikedBy.size() : 0;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
