package com.example.model.onboarding;

import java.util.List;

public class OnboardingResponse {
    private String id;
    private String userId;
    private String gender;
    private List<String> motivation;
    private List<String> healthIssues;
    private String fitnessExperience;
    private String createdAt;
    private Integer age;
    private Double weight;
    private Double height;

    public OnboardingResponse() {}

    public OnboardingResponse(String id, String userId, String gender, List<String> motivation, 
                            List<String> healthIssues, String fitnessExperience, String createdAt,
                            Integer age, Double weight, Double height) {
        this.id = id;
        this.userId = userId;
        this.gender = gender;
        this.motivation = motivation;
        this.healthIssues = healthIssues;
        this.fitnessExperience = fitnessExperience;
        this.createdAt = createdAt;
        this.age = age;
        this.weight = weight;
        this.height = height;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public List<String> getMotivation() { return motivation; }
    public void setMotivation(List<String> motivation) { this.motivation = motivation; }

    public List<String> getHealthIssues() { return healthIssues; }
    public void setHealthIssues(List<String> healthIssues) { this.healthIssues = healthIssues; }

    public String getFitnessExperience() { return fitnessExperience; }
    public void setFitnessExperience(String fitnessExperience) { this.fitnessExperience = fitnessExperience; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
} 