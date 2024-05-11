package com.example.goodlink.FireBase;

public class RatingManager {
    private String rating;
    private String currentUserRating;

    public RatingManager() {

    }

    public RatingManager(String rating, String currentUserRating) {
        this.rating = rating;
        this.currentUserRating = currentUserRating;
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
}
