package com.example.network;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static Context appContext = null;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (appContext == null) {
            throw new IllegalStateException("ApiClient not initialized. Call ApiClient.init(context) first.");
        }
        
        if (retrofit == null) {
            String baseUrl = NetworkConfig.getBaseUrl(appContext);
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    // Method to get base URL for debugging
    public static String getBaseUrl() {
        if (appContext == null) {
            return "http://10.0.2.2:8080/"; // fallback
        }
        return NetworkConfig.getBaseUrl(appContext);
    }
    
    // Method to change base URL if necessary
    public static void setBaseUrl(String baseUrl) {
        if (appContext != null) {
            NetworkConfig.setBaseUrl(appContext, baseUrl);
        }
        // Rebuild retrofit with new URL
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    // Method to reset to auto-detect
    public static void resetToAuto() {
        if (appContext != null) {
            NetworkConfig.resetToAuto(appContext);
        }
        retrofit = null; // Force rebuild on next getClient() call
    }
}
