package com.example.network;

import com.example.model.*;
import com.example.model.onboarding.*;
import com.example.model.auth.*;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface ApiService {
    
    // Health check
    @GET("api/users/health")
    Call<Map<String, String>> healthCheck();
    
    // User APIs
    @POST("api/users/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);
    
    @POST("api/users/login")
    Call<User> loginUser(@Body Map<String, String> loginRequest);
    
    @GET("api/users/{userId}/personal-data")
    Call<PersonalData> getPersonalData(@Path("userId") String userId);
    
    @GET("api/users/{userId}")
    Call<User> getUserById(@Path("userId") String userId);

    @POST("api/workout-sessions")
    Call<WorkoutSession> saveWorkoutSession(@Body WorkoutSession session);

    @GET("api/kcal/history")
    Call<List<KcalRecord>> getHistory(@Query("userId") String userId);

    // Questionnaire APIs
    @POST("api/questionnaires")
    Call<Questionnaire> saveQuestionnaire(@Body Questionnaire questionnaire);

    @GET("api/questionnaires/{userId}")
    Call<List<Questionnaire>> getQuestionnairesByUserId(@Path("userId") String userId);

    // Onboarding APIs
    @POST("api/onboarding/save")
    Call<OnboardingResponse> saveOnboardingData(@Query("userId") String userId, @Body Map<String, Object> onboardingData);

    @GET("api/onboarding/data")
    Call<OnboardingResponse> getOnboardingData(@Query("userId") String userId);

    // Meal Plan APIs
    @POST("api/meal-plans/generate")
    Call<MealPlan> generateMealPlan(@Query("userId") String userId, @Body NutritionGoals nutritionGoals);

    @GET("api/meal-plans/{userId}/{date}")
    Call<MealPlan> getMealPlan(@Path("userId") String userId, @Path("date") String date);

    @POST("api/meal-plans")
    Call<MealPlan> saveMealPlan(@Body MealPlan mealPlan);

    @PUT("api/meal-plans/{id}")
    Call<MealPlan> updateMealPlan(@Path("id") String id, @Body MealPlan mealPlan);

    @DELETE("api/meal-plans/{id}")
    Call<Void> deleteMealPlan(@Path("id") String id);

    // Nutrition Goals APIs
    @POST("api/nutrition-goals")
    Call<NutritionGoals> saveNutritionGoals(@Body NutritionGoals nutritionGoals);

    @GET("api/nutrition-goals/{userId}")
    Call<NutritionGoals> getNutritionGoals(@Path("userId") String userId);

    @PUT("api/nutrition-goals/{userId}")
    Call<NutritionGoals> updateNutritionGoals(@Path("userId") String userId, @Body NutritionGoals nutritionGoals);

    // Meal APIs
    @GET("api/meals")
    Call<List<Meal>> getAllMeals();

    @GET("api/meals/{id}")
    Call<Meal> getMealById(@Path("id") String id);

    @POST("api/meals")
    Call<Meal> createMeal(@Body Meal meal);

    @GET("api/meals/search")
    Call<List<Meal>> searchMealsByName(@Query("name") String name);

    @GET("api/meals/calories")
    Call<List<Meal>> getMealsByCalorieRange(@Query("minCalories") int minCalories, @Query("maxCalories") int maxCalories);
}
