package com.example.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonalData {
    @SerializedName("_id")
    private String id;
    @SerializedName("userId")
    private String userId;
    private String motivation;
    @SerializedName("healthcareIssues")
    private List<String> healthcareIssues;
    private List<String> injuries;
    @SerializedName("dietaryRestrictions")
    private List<String> dietaryRestrictions;
    @SerializedName("fitnessExperience")
    private String fitnessExperience;
    private String goal;
    private String activityLevel;

    public PersonalData(String userId, String motivation, List<String> healthcareIssues,
                       List<String> injuries, List<String> dietaryRestrictions, String fitnessExperience,
                        String goal, String activityLevel) {
        this.userId = userId;
        this.motivation = motivation;
        this.healthcareIssues = healthcareIssues;
        this.injuries = injuries;
        this.dietaryRestrictions = dietaryRestrictions;
        this.fitnessExperience = fitnessExperience;
        this.goal = goal;
        this.activityLevel = activityLevel;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMotivation() {
        return motivation;
    }

    public List<String> getHealthcareIssues() {
        return healthcareIssues;
    }

    public List<String> getInjuries() {
        return injuries;
    }

    public List<String> getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public String getFitnessExperience() {
        return fitnessExperience;
    }

    public String getGoal() {
        return goal;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public void setHealthcareIssues(List<String> healthcareIssues) {
        this.healthcareIssues = healthcareIssues;
    }

    public void setInjuries(List<String> injuries) {
        this.injuries = injuries;
    }

    public void setDietaryRestrictions(List<String> dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public void setFitnessExperience(String fitnessExperience) {
        this.fitnessExperience = fitnessExperience;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }
} 