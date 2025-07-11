package com.example.service;

import android.content.Context;
import android.util.Log;

import com.example.model.MealPlan;
import com.example.model.NutritionGoals;
import com.example.model.PersonalData;
import com.example.network.ApiService;
import com.example.network.RetrofitInstance;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealPlanService {
    private static final String TAG = "MealPlanService";
    private ApiService apiService;
    private Context context;

    public MealPlanService(Context context) {
        this.context = context;
        this.apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
    }

    public interface MealPlanCallback {
        void onSuccess(MealPlan mealPlan);
        void onError(String error);
    }

    public interface NutritionGoalsCallback {
        void onSuccess(NutritionGoals nutritionGoals);
        void onError(String error);
    }

    public interface PersonalDataCallback {
        void onSuccess(PersonalData personalData);
        void onError(String error);
    }

    /**
     * Generate a personalized meal plan based on user's nutrition goals
     */
    public void generateMealPlan(String userId, NutritionGoals nutritionGoals, MealPlanCallback callback) {
        Log.d(TAG, "Generating meal plan for user: " + userId);
        Log.d(TAG, "Nutrition goals: " + nutritionGoals.toString());
        
        // First, get user's personal data (dietary restrictions, preferences)
        getPersonalData(userId, new PersonalDataCallback() {
            @Override
            public void onSuccess(PersonalData personalData) {
                Log.d(TAG, "Personal data retrieved successfully");
                Log.d(TAG, "Personal data dietary restrictions: " + personalData.getDietaryRestrictions());
                
                // Merge personal data with nutrition goals
                if (personalData.getDietaryRestrictions() != null) {
                    nutritionGoals.setDietaryRestrictions(personalData.getDietaryRestrictions());
                    Log.d(TAG, "Merged dietary restrictions from personal data");
                }
                
                // Call the API to generate meal plan
                Log.d(TAG, "Making API call to generate meal plan");
                Call<MealPlan> call = apiService.generateMealPlan(userId, nutritionGoals);
                call.enqueue(new Callback<MealPlan>() {
                    @Override
                    public void onResponse(Call<MealPlan> call, Response<MealPlan> response) {
                        Log.d(TAG, "API response received - Code: " + response.code());
                        
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Meal plan generated successfully");
                            Log.d(TAG, "Generated meal plan: " + response.body().toString());
                            callback.onSuccess(response.body());
                        } else {
                            String error = "Failed to generate meal plan: " + response.code();
                            Log.e(TAG, error);
                            Log.e(TAG, "Response body: " + response.body());
                            callback.onError(error);
                        }
                    }

                    @Override
                    public void onFailure(Call<MealPlan> call, Throwable t) {
                        String error = "Network error: " + t.getMessage();
                        Log.e(TAG, error, t);
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to get personal data: " + error);
                callback.onError("Failed to get user preferences: " + error);
            }
        });
    }

    /**
     * Save nutrition goals for a user
     */
    public void saveNutritionGoals(NutritionGoals nutritionGoals, NutritionGoalsCallback callback) {
        Log.d(TAG, "Saving nutrition goals for user: " + nutritionGoals.getUserId());
        
        Call<NutritionGoals> call = apiService.saveNutritionGoals(nutritionGoals);
        call.enqueue(new Callback<NutritionGoals>() {
            @Override
            public void onResponse(Call<NutritionGoals> call, Response<NutritionGoals> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Nutrition goals saved successfully");
                    callback.onSuccess(response.body());
                } else {
                    String error = "Failed to save nutrition goals: " + response.code();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<NutritionGoals> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }

    /**
     * Get nutrition goals for a user
     */
    public void getNutritionGoals(String userId, NutritionGoalsCallback callback) {
        Log.d(TAG, "Getting nutrition goals for user: " + userId);
        
        Call<NutritionGoals> call = apiService.getNutritionGoals(userId);
        call.enqueue(new Callback<NutritionGoals>() {
            @Override
            public void onResponse(Call<NutritionGoals> call, Response<NutritionGoals> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Nutrition goals retrieved successfully");
                    callback.onSuccess(response.body());
                } else {
                    String error = "Failed to get nutrition goals: " + response.code();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<NutritionGoals> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }

    /**
     * Get meal plan for a specific date
     */
    public void getMealPlan(String userId, String date, MealPlanCallback callback) {
        Log.d(TAG, "Getting meal plan for user: " + userId + " on date: " + date);
        Call<List<MealPlan>> call = apiService.getMealPlan(userId, date);
        call.enqueue(new Callback<List<MealPlan>>() {
            @Override
            public void onResponse(Call<List<MealPlan>> call, Response<List<MealPlan>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Lấy meal plan mới nhất (giả sử cuối danh sách là mới nhất)
                    MealPlan latest = response.body().get(response.body().size() - 1);
                    Log.d(TAG, "Meal plan retrieved successfully");
                    callback.onSuccess(latest);
                } else {
                    String error = "No meal plan found";
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<List<MealPlan>> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }

    /**
     * Save meal plan
     */
    public void saveMealPlan(MealPlan mealPlan, MealPlanCallback callback) {
        Log.d(TAG, "Saving meal plan for user: " + mealPlan.getUserId());
        
        Call<MealPlan> call = apiService.saveMealPlan(mealPlan);
        call.enqueue(new Callback<MealPlan>() {
            @Override
            public void onResponse(Call<MealPlan> call, Response<MealPlan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Meal plan saved successfully");
                    callback.onSuccess(response.body());
                } else {
                    String error = "Failed to save meal plan: " + response.code();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<MealPlan> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }

    /**
     * Get personal data for a user
     */
    private void getPersonalData(String userId, PersonalDataCallback callback) {
        Log.d(TAG, "Getting personal data for user: " + userId);
        
        Call<PersonalData> call = apiService.getPersonalData(userId);
        call.enqueue(new Callback<PersonalData>() {
            @Override
            public void onResponse(Call<PersonalData> call, Response<PersonalData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Personal data retrieved successfully");
                    callback.onSuccess(response.body());
                } else {
                    String error = "Failed to get personal data: " + response.code();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<PersonalData> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }
} 