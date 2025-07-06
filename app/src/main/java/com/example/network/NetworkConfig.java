package com.example.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class NetworkConfig {
    private static final String TAG = "NetworkConfig";
    private static final String PREF_NAME = "network_config";
    private static final String KEY_BASE_URL = "base_url";
    
    // Default URLs
    private static final String EMULATOR_URL = "http://10.0.2.2:8080/";
    private static final String DEVICE_URL = "http://192.168.88.168:8080/"; // Laptop IP
    private static final String LOCALHOST_URL = "http://localhost:8080/";
    
    private static String currentBaseUrl = null;
    
    /**
     * Get the appropriate base URL based on device type
     */
    public static String getBaseUrl(Context context) {
        if (currentBaseUrl != null) {
            return currentBaseUrl;
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedUrl = prefs.getString(KEY_BASE_URL, null);
        
        if (savedUrl != null) {
            currentBaseUrl = savedUrl;
            Log.d(TAG, "Using saved URL: " + currentBaseUrl);
            return currentBaseUrl;
        }
        
        // Auto-detect: if running on emulator, use emulator URL, otherwise use device URL
        if (isEmulator()) {
            currentBaseUrl = EMULATOR_URL;
            Log.d(TAG, "Detected emulator, using: " + currentBaseUrl);
        } else {
            currentBaseUrl = DEVICE_URL;
            Log.d(TAG, "Detected real device, using: " + currentBaseUrl);
        }
        
        // Save for future use
        prefs.edit().putString(KEY_BASE_URL, currentBaseUrl).apply();
        
        return currentBaseUrl;
    }
    
    /**
     * Set custom base URL (for testing different configurations)
     */
    public static void setBaseUrl(Context context, String baseUrl) {
        currentBaseUrl = baseUrl;
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
               .edit()
               .putString(KEY_BASE_URL, baseUrl)
               .apply();
        Log.d(TAG, "Set custom URL: " + baseUrl);
    }
    
    /**
     * Reset to auto-detect mode
     */
    public static void resetToAuto(Context context) {
        currentBaseUrl = null;
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
               .edit()
               .remove(KEY_BASE_URL)
               .apply();
        Log.d(TAG, "Reset to auto-detect mode");
    }
    
    /**
     * Check if running on emulator
     */
    private static boolean isEmulator() {
        return android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(android.os.Build.PRODUCT);
    }
    
    /**
     * Get all available URLs for testing
     */
    public static String[] getAvailableUrls() {
        return new String[]{
            EMULATOR_URL + " (Emulator)",
            DEVICE_URL + " (Real Device)",
            LOCALHOST_URL + " (Localhost)",
            "http://192.168.1.100:8080/ (Common Router IP)",
            "http://192.168.0.100:8080/ (Common Router IP 2)"
        };
    }
} 