package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class MealPlan {
    @SerializedName("_id")
    private String id;
    private String userId;
    private String date;
    private List<DailyMeal> meals;
    private NutritionSummary nutritionSummary;
    private NutritionGoals nutritionGoals;
    private String status; // "active", "completed", "cancelled"

    public MealPlan() {}

    public MealPlan(String userId, String date, List<DailyMeal> meals, 
                   NutritionSummary nutritionSummary, NutritionGoals nutritionGoals) {
        this.userId = userId;
        this.date = date;
        this.meals = meals;
        this.nutritionSummary = nutritionSummary;
        this.nutritionGoals = nutritionGoals;
        this.status = "active";
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getDate() { return date; }
    public List<DailyMeal> getMeals() { return meals; }
    public NutritionSummary getNutritionSummary() { return nutritionSummary; }
    public NutritionGoals getNutritionGoals() { return nutritionGoals; }
    public String getStatus() { return status; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setDate(String date) { this.date = date; }
    public void setMeals(List<DailyMeal> meals) { this.meals = meals; }
    public void setNutritionSummary(NutritionSummary nutritionSummary) { this.nutritionSummary = nutritionSummary; }
    public void setNutritionGoals(NutritionGoals nutritionGoals) { this.nutritionGoals = nutritionGoals; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "MealPlan{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", date='" + date + '\'' +
                ", meals=" + meals +
                ", nutritionSummary=" + nutritionSummary +
                ", nutritionGoals=" + nutritionGoals +
                ", status='" + status + '\'' +
                '}';
    }

    // Inner class for daily meals
    public static class DailyMeal {
        private String mealType; // "breakfast", "lunch", "dinner", "snack"
        private String time;
        private List<Meal> meals;
        private int portionSize;
        private String notes;

        public DailyMeal() {}

        public DailyMeal(String mealType, String time, List<Meal> meals, int portionSize, String notes) {
            this.mealType = mealType;
            this.time = time;
            this.meals = meals;
            this.portionSize = portionSize;
            this.notes = notes;
        }

        // Getters
        public String getMealType() { return mealType; }
        public String getTime() { return time; }
        public List<Meal> getMeals() { return meals == null ? java.util.Collections.emptyList() : meals; }
        public int getPortionSize() { return portionSize; }
        public String getNotes() { return notes; }

        // Setters
        public void setMealType(String mealType) { this.mealType = mealType; }
        public void setTime(String time) { this.time = time; }
        public void setMeals(List<Meal> meals) { this.meals = meals; }
        public void setPortionSize(int portionSize) { this.portionSize = portionSize; }
        public void setNotes(String notes) { this.notes = notes; }

        @Override
        public String toString() {
            return "DailyMeal{" +
                    "mealType='" + mealType + '\'' +
                    ", time='" + time + '\'' +
                    ", meals=" + meals +
                    ", portionSize=" + portionSize +
                    ", notes='" + notes + '\'' +
                    '}';
        }
    }

    // Inner class for nutrition summary
    public static class NutritionSummary {
        private int totalCalories;
        private double totalProtein;
        private double totalCarbohydrates;
        private double totalFat;
        private int targetCalories;
        private double proteinPercentage;
        private double carbohydratePercentage;
        private double fatPercentage;

        public NutritionSummary() {}

        public NutritionSummary(int totalCalories, double totalProtein, double totalCarbohydrates, 
                              double totalFat, int targetCalories, double proteinPercentage, 
                              double carbohydratePercentage, double fatPercentage) {
            this.totalCalories = totalCalories;
            this.totalProtein = totalProtein;
            this.totalCarbohydrates = totalCarbohydrates;
            this.totalFat = totalFat;
            this.targetCalories = targetCalories;
            this.proteinPercentage = proteinPercentage;
            this.carbohydratePercentage = carbohydratePercentage;
            this.fatPercentage = fatPercentage;
        }

        // Getters
        public int getTotalCalories() { return totalCalories; }
        public double getTotalProtein() { return totalProtein; }
        public double getTotalCarbohydrates() { return totalCarbohydrates; }
        public double getTotalFat() { return totalFat; }
        public int getTargetCalories() { return targetCalories; }
        public double getProteinPercentage() { return proteinPercentage; }
        public double getCarbohydratePercentage() { return carbohydratePercentage; }
        public double getFatPercentage() { return fatPercentage; }

        // Setters
        public void setTotalCalories(int totalCalories) { this.totalCalories = totalCalories; }
        public void setTotalProtein(double totalProtein) { this.totalProtein = totalProtein; }
        public void setTotalCarbohydrates(double totalCarbohydrates) { this.totalCarbohydrates = totalCarbohydrates; }
        public void setTotalFat(double totalFat) { this.totalFat = totalFat; }
        public void setTargetCalories(int targetCalories) { this.targetCalories = targetCalories; }
        public void setProteinPercentage(double proteinPercentage) { this.proteinPercentage = proteinPercentage; }
        public void setCarbohydratePercentage(double carbohydratePercentage) { this.carbohydratePercentage = carbohydratePercentage; }
        public void setFatPercentage(double fatPercentage) { this.fatPercentage = fatPercentage; }

        @Override
        public String toString() {
            return "NutritionSummary{" +
                    "totalCalories=" + totalCalories +
                    ", totalProtein=" + totalProtein +
                    ", totalCarbohydrates=" + totalCarbohydrates +
                    ", totalFat=" + totalFat +
                    ", targetCalories=" + targetCalories +
                    ", proteinPercentage=" + proteinPercentage +
                    ", carbohydratePercentage=" + carbohydratePercentage +
                    ", fatPercentage=" + fatPercentage +
                    '}';
        }
    }
} 