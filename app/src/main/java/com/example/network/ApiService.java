package com.example.network;

import com.example.model.Meal;
import com.example.model.MealPlan;
import com.example.model.NutritionGoals;
import com.example.model.onboarding.OnboardingResponse;
import com.example.model.PersonalData;
import com.example.model.auth.RegisterRequest;
import com.example.model.auth.RegisterResponse;
import com.example.model.auth.User;
import com.example.model.Workout;
import com.example.model.WorkoutSession;

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
}
