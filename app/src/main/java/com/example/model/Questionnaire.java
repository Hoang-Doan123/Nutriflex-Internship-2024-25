package com.example.model;

public class Questionnaire {
    private String id;
    private String userId;
    private String workoutType;

    public Questionnaire() {}

    public Questionnaire(String userId, String workoutType) {
        this.userId = userId;
        this.workoutType = workoutType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }
} 