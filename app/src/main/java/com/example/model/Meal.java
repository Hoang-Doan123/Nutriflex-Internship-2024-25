package com.example.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Meal {
    @SerializedName("_id")
    private String id;
    private String name;
    private int calories;
    private double protein;
    private double carbohydrate;
    private double fat;

    public Meal() {}

    public Meal(String id, String name, int calories, double protein, double carbohydrate, double fat) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrate = carbohydrate;
        this.fat = fat;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getCarbohydrate() { return carbohydrate; }
    public void setCarbohydrate(double carbohydrate) { this.carbohydrate = carbohydrate; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    @NonNull
    @Override
    public String toString() {
        return "Meal{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", calories=" + calories +
                ", protein=" + protein +
                ", carbohydrate=" + carbohydrate +
                ", fat=" + fat +
                '}';
    }
} 