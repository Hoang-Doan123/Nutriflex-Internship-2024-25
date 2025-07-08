package com.example.model.auth;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String gender;
    private String motivation;
    @SerializedName("healthcareIssues")
    private List<String> healthcareIssues;
    private List<String> injuries;
    @SerializedName("dietaryRestrictions")
    private List<String> dietaryRestrictions;
    @SerializedName("fitnessExperience")
    private String fitnessExperience;
    private Integer age;
    private Double weight;
    private Double height;
    private String goal;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String email, String password, String gender, 
                          String motivation, List<String> healthcareIssues, 
                          List<String> injuries, List<String> dietaryRestrictions, 
                          String fitnessExperience, String goal) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.motivation = motivation;
        this.healthcareIssues = healthcareIssues;
        this.injuries = injuries;
        this.dietaryRestrictions = dietaryRestrictions;
        this.fitnessExperience = fitnessExperience;
        this.goal = goal;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
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

    public Integer getAge() {
        return age;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getHeight() {
        return height;
    }

    public String getGoal() {
        return goal;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
} 