package com.example.goodlink.FireBaseManager;

public class ManagerComment {
    private String userName;
    private String userComment;
    private String repositoryId;

    public ManagerComment(){

    }

    public ManagerComment(String userName, String userComment, String repositoryId) {
        this.userName = userName;
        this.userComment = userComment;
        this.repositoryId = repositoryId;
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
}
