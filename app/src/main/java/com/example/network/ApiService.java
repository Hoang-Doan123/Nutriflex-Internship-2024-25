package com.example.network;

import com.example.model.OnboardingResponse;
import com.example.model.PersonalData;
import com.example.model.RegisterRequest;
import com.example.model.RegisterResponse;
import com.example.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface ApiService {
    
    // User APIs
    @POST("api/users/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);
    
    @POST("api/users/login")
    Call<User> loginUser(@Body Map<String, String> loginRequest);
    
    @GET("api/users/{userId}/personal-data")
    Call<PersonalData> getPersonalData(@Path("userId") String userId);
}
