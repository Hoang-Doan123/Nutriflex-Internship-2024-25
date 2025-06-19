package com.example.network;

import com.example.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/users/register")
    Call<User> registerUser(@Body User user);
}
