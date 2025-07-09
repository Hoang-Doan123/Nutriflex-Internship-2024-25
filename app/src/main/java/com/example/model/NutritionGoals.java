package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NutritionGoals {
    @SerializedName("_id")
    private String id;
    private String userId;
    private int dailyCalories;
    private double proteinPercentage;
    private double carbohydratePercentage;
    private double fatPercentage;
    private List<String> dietaryRestrictions;
    private List<String> foodPreferences;
    private List<String> allergies;
    private String mealPlanType; // "weight_loss", "muscle_gain", "maintenance", "general_health"
    private int mealsPerDay;
    private boolean includeSnacks;

    public NutritionGoals() {}

    public NutritionGoals(String userId, int dailyCalories, double proteinPercentage, 
                         double carbohydratePercentage, double fatPercentage, 
                         List<String> dietaryRestrictions, List<String> foodPreferences, 
                         List<String> allergies, String mealPlanType, int mealsPerDay, 
                         boolean includeSnacks) {
        this.userId = userId;
        this.dailyCalories = dailyCalories;
        this.proteinPercentage = proteinPercentage;
        this.carbohydratePercentage = carbohydratePercentage;
        this.fatPercentage = fatPercentage;
        this.dietaryRestrictions = dietaryRestrictions;
        this.foodPreferences = foodPreferences;
        this.allergies = allergies;
        this.mealPlanType = mealPlanType;
        this.mealsPerDay = mealsPerDay;
        this.includeSnacks = includeSnacks;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public int getDailyCalories() { return dailyCalories; }
    public double getProteinPercentage() { return proteinPercentage; }
    public double getCarbohydratePercentage() { return carbohydratePercentage; }
    public double getFatPercentage() { return fatPercentage; }
    public List<String> getDietaryRestrictions() { return dietaryRestrictions; }
    public List<String> getFoodPreferences() { return foodPreferences; }
    public List<String> getAllergies() { return allergies; }
    public String getMealPlanType() { return mealPlanType; }
    public int getMealsPerDay() { return mealsPerDay; }
    public boolean isIncludeSnacks() { return includeSnacks; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setDailyCalories(int dailyCalories) { this.dailyCalories = dailyCalories; }
    public void setProteinPercentage(double proteinPercentage) { this.proteinPercentage = proteinPercentage; }
    public void setCarbohydratePercentage(double carbohydratePercentage) { this.carbohydratePercentage = carbohydratePercentage; }
    public void setFatPercentage(double fatPercentage) { this.fatPercentage = fatPercentage; }
    public void setDietaryRestrictions(List<String> dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }
    public void setFoodPreferences(List<String> foodPreferences) { this.foodPreferences = foodPreferences; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    public void setMealPlanType(String mealPlanType) { this.mealPlanType = mealPlanType; }
    public void setMealsPerDay(int mealsPerDay) { this.mealsPerDay = mealsPerDay; }
    public void setIncludeSnacks(boolean includeSnacks) { this.includeSnacks = includeSnacks; }

    @Override
    public String toString() {
        return "NutritionGoals{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", dailyCalories=" + dailyCalories +
                ", proteinPercentage=" + proteinPercentage +
                ", carbohydratePercentage=" + carbohydratePercentage +
                ", fatPercentage=" + fatPercentage +
                ", dietaryRestrictions=" + dietaryRestrictions +
                ", foodPreferences=" + foodPreferences +
                ", allergies=" + allergies +
                ", mealPlanType='" + mealPlanType + '\'' +
                ", mealsPerDay=" + mealsPerDay +
                ", includeSnacks=" + includeSnacks +
                '}';
    }
} 