package com.example;

import com.example.authpackage.AuthAPIService;
import com.example.videopackage.VideoAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String url = "https://android-backend-tech-c52e01da23ae.herokuapp.com/";

    private static Retrofit retrofit = null;

    public static AuthAPIService getAuthApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AuthAPIService.class);
    }

    public static VideoAPI getVideoApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(VideoAPI.class);
    }
}
