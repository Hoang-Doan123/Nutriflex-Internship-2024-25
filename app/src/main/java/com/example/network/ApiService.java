package com.example.network;

import com.example.model.User;

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
    Call<User> registerUser(@Body User user);
    
    @POST("api/users/login")
    Call<User> loginUser(@Body Map<String, String> loginRequest);
}
