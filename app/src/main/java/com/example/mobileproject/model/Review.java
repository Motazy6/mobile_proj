package com.example.mobileproject.model;

public class Review {
    private int id;
    private int housingId;
    private int userId;
    private String userName;
    private float rating;
    private String comment;

    public Review() {}

    public Review(int id, int housingId, int userId, int rating, String comment) {
        this.id = id;
        this.housingId = housingId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHousingId() {
        return housingId;
    }

    public void setHousingId(int housingId) {
        this.housingId = housingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

