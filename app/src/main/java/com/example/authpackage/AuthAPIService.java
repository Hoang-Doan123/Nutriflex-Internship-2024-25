package com.example.authpackage;

import com.example.requestpackage.ForgotPasswordRequest;
import com.example.requestpackage.LoginRequest;
import com.example.requestpackage.RegisterRequest;
import com.example.responsepackage.ForgotPasswordResponse;
import com.example.responsepackage.LoginResponse;
import com.example.responsepackage.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthAPIService {
    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("signup")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("password-reset")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);
}
