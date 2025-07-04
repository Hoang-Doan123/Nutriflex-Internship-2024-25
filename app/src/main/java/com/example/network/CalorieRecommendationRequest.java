package com.example.network;

public class CalorieRecommendationRequest {
    private long userId;
    private int caloriesBurned;

    public CalorieRecommendationRequest(long userId, int caloriesBurned) {
        this.userId = userId;
        this.caloriesBurned = caloriesBurned;
    }

    public long getUserId() { return userId; }
    public int getCaloriesBurned() { return caloriesBurned; }
} 