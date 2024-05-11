package com.example.goodlink.FireBase;

public class RatingManager {
    private String rating;
    private String userRating;
    private String playlistRated;

    public RatingManager(String rating, String userRating, String avaliado) {
        this.rating = rating;
        this.userRating = userRating;
        this.playlistRated = avaliado;
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

    public String getPlaylistRated() {
        return playlistRated;
    }

    public void setPlaylistRated(String playlistRated) {
        this.playlistRated = playlistRated;
    }
}