package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "NutriFlexSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_WEIGHT = "userWeight";
    private static final String KEY_USER_HEIGHT = "userHeight";
    private static final String KEY_USER_AGE = "userAge";
    private static final String KEY_USER_GENDER = "userGender";
    
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    public void setLoginSession(String userId, String email) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    public void setUserProfile(String userId, String email, Double weight, Double height, Integer age, String gender) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        if (weight != null) editor.putFloat(KEY_USER_WEIGHT, weight.floatValue());
        if (height != null) editor.putFloat(KEY_USER_HEIGHT, height.floatValue());
        if (age != null) editor.putInt(KEY_USER_AGE, age);
        if (gender != null) editor.putString(KEY_USER_GENDER, gender);
        editor.apply();
    }
    
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "1"); // Default to "1" for demo
    }
    
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }
    
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public Double getUserWeight() {
        float weight = sharedPreferences.getFloat(KEY_USER_WEIGHT, 70.0f); // Default to 70kg
        return (double) weight;
    }
    
    public Double getUserHeight() {
        float height = sharedPreferences.getFloat(KEY_USER_HEIGHT, 170.0f); // Default to 170cm
        return (double) height;
    }
    
    public Integer getUserAge() {
        return sharedPreferences.getInt(KEY_USER_AGE, 25); // Default to 25 years
    }
    
    public String getUserGender() {
        return sharedPreferences.getString(KEY_USER_GENDER, "male"); // Default to male
    }
    
    public void logout() {
        editor.clear();
        editor.apply();
    }
} 