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
}
