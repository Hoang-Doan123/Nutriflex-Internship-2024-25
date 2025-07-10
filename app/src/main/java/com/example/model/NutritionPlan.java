package com.example.model;

import com.google.gson.annotations.SerializedName;

public class NutritionPlan {
    @SerializedName("_id")
    private String id;
    private String userId;
    private String sessionId;
    private int recommendedCalories;
    private String createdAt;

    public NutritionPlan() {}

    public NutritionPlan(String userId, String sessionId, int recommendedCalories, String createdAt) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.recommendedCalories = recommendedCalories;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getSessionId() { return sessionId; }
    public int getRecommendedCalories() { return recommendedCalories; }
    public String getCreatedAt() { return createdAt; }

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setRecommendedCalories(int recommendedCalories) { this.recommendedCalories = recommendedCalories; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "NutritionPlan{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", recommendedCalories=" + recommendedCalories +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
} 