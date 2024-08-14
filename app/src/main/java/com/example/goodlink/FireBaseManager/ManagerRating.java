package com.example.goodlink.FireBaseManager;

public class ManagerRating {
    private String rating;
    private String userRating;
    private String repositoryRated;

    public ManagerRating(String rating, String userRating, String avaliado) {
        this.rating = rating;
        this.userRating = userRating;
        this.repositoryRated = avaliado;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getRepositoryRated() {
        return repositoryRated;
    }

    public void setRepositoryRated(String repositoryRated) {
        this.repositoryRated = repositoryRated;
    }
}