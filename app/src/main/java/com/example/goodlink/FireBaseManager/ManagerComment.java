package com.example.goodlink.FireBaseManager;

public class ManagerComment {
    private String userName;
    private String userComment;
    private String playlistId;

    public ManagerComment(){

    }

    public ManagerComment(String userName, String userComment, String playlistId) {
        this.userName = userName;
        this.userComment = userComment;
        this.playlistId = playlistId;
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

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}
