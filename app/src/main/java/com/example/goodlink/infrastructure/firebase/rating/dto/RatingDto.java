package com.example.goodlink.infrastructure.firebase.rating.dto;

public class RatingDto {
    public int rating;
    public String userId;
    public String repositoryId;

    public RatingDto() {}

    public RatingDto(int rating, String userId, String repositoryId) {
        this.rating = rating;
        this.userId = userId;
        this.repositoryId = repositoryId;
    }
}