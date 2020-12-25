package com.bookie_tbh.bookie.ui.ratingReview;

public class RatingReview {

    private String userName, providerName, review;
    private int rating;

    public RatingReview() {
    }

    public RatingReview(String userName, String providerName, String review, int rating) {
        this.userName = userName;
        this.providerName = providerName;
        this.review = review;
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getReview() {
        return review;
    }

    public int getRating() {
        return rating;
    }
}
