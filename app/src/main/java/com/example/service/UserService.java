package com.example.service;

import android.content.Context;
import android.util.Log;

import com.example.model.auth.User;
import com.example.network.ApiService;
import com.example.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private static final String TAG = "UserService";
    private ApiService apiService;
    private Context context;

    public UserService(Context context) {
        this.context = context;
        this.apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    /**
     * Get user data from MongoDB database by userId
     */
    public void getUserById(String userId, UserCallback callback) {
        Log.d(TAG, "Fetching user data for userId: " + userId);
        
        Call<User> call = apiService.getUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d(TAG, "User data retrieved successfully: " + user.getName() + 
                          ", Weight: " + user.getWeight() + "kg");
                    callback.onSuccess(user);
                } else {
                    String error = "Failed to get user data: " + response.code();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }
} 