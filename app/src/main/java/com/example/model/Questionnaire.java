package com.example.model;

import java.util.List;

public class Questionnaire {
    private String id;
    private String userId;
    private String workoutType;
    private String fitnessExperience;
    private List<String> injuries;
    private List<String> healthcareIssues;
    private List<String> dietaryRestrictions;
    private String goal;
    private String motivation;

    public Questionnaire() {}

    public Questionnaire(String userId, String workoutType, String fitnessExperience, List<String> injuries, List<String> healthcareIssues, List<String> dietaryRestrictions, String goal, String motivation) {
        this.userId = userId;
        this.workoutType = workoutType;
        this.fitnessExperience = fitnessExperience;
        this.injuries = injuries;
        this.healthcareIssues = healthcareIssues;
        this.dietaryRestrictions = dietaryRestrictions;
        this.goal = goal;
        this.motivation = motivation;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getWorkoutType() { return workoutType; }
    public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }
    public String getFitnessExperience() { return fitnessExperience; }
    public void setFitnessExperience(String fitnessExperience) { this.fitnessExperience = fitnessExperience; }
    public List<String> getInjuries() { return injuries; }
    public void setInjuries(List<String> injuries) { this.injuries = injuries; }
    public List<String> getHealthcareIssues() { return healthcareIssues; }
    public void setHealthcareIssues(List<String> healthcareIssues) { this.healthcareIssues = healthcareIssues; }
    public List<String> getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(List<String> dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public String getMotivation() { return motivation; }
    public void setMotivation(String motivation) { this.motivation = motivation; }
} 