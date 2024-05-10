package com.example.goodlink.FireBase;

public class RatingManager {
    private String rating;
    private String currentUserRating;
    private String UserRated;

    public RatingManager(){

    }

    public RatingManager(String rating, String avaliador, String UserRated) {
        this.rating = rating;
        this.currentUserRating = avaliador;
        this.UserRated = UserRated;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCurrentUserRating() {
        return currentUserRating;
    }

    public void setCurrentUserRating(String currentUserRating) {
        this.currentUserRating = currentUserRating;
    }

    public String getUserRated() {
        return UserRated;
    }

    public void setUserRated(String userRated) {
        this.UserRated = userRated;
    }
}
