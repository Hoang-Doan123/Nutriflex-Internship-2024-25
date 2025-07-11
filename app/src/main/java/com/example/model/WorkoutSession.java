package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public class WorkoutSession {
    @SerializedName("_id")
    private String id;
    @SerializedName("userId")
    private String userId;
    @SerializedName("type")
    private String type;
    @SerializedName("startTime")
    private String startTime; // ISO 8601 string
    @SerializedName("endTime")
    private String endTime;   // ISO 8601 string
    @SerializedName("caloriesBurned")
    private float caloriesBurned;
    @SerializedName("distance")
    private float distance;
    @SerializedName("steps")
    private int steps;
    @SerializedName("heartRateAvg")
    private float heartRateAvg;

    public WorkoutSession(String userId, String type, String startTime, String endTime, float caloriesBurned, float distance, int steps, float heartRateAvg) {
        this.userId = userId;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.caloriesBurned = caloriesBurned;
        this.distance = distance;
        this.steps = steps;
        this.heartRateAvg = heartRateAvg;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public float getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(float caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }
    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }
    public float getHeartRateAvg() { return heartRateAvg; }
    public void setHeartRateAvg(float heartRateAvg) { this.heartRateAvg = heartRateAvg; }
} 