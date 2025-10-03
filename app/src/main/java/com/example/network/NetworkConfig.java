package com.example.network;

import android.content.*;
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
        // Migration: replace old hardcoded IP with localhost for real devices
        if (savedUrl != null && savedUrl.contains("192.168.88.168")) {
            savedUrl = LOCALHOST_URL;
            prefs.edit().putString(KEY_BASE_URL, savedUrl).apply();
            Log.d(TAG, "Migrated saved URL to: " + savedUrl);
        }
        
        if (savedUrl != null) {
            // If running on emulator, always force the emulator URL
            if (isEmulator() && !EMULATOR_URL.equals(savedUrl)) {
                currentBaseUrl = EMULATOR_URL;
                prefs.edit().putString(KEY_BASE_URL, currentBaseUrl).apply();
                Log.d(TAG, "Overriding saved URL for emulator: " + currentBaseUrl);
                return currentBaseUrl;
            }
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
        boolean isEmulator = android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(android.os.Build.PRODUCT)
                || android.os.Build.MODEL.contains("sdk_gphone")
                || android.os.Build.MODEL.contains("Pixel")
                || android.os.Build.MODEL.contains("Android SDK built for x86_64");
        
        Log.d(TAG, "Device detection:");
        Log.d(TAG, "FINGERPRINT: " + android.os.Build.FINGERPRINT);
        Log.d(TAG, "MODEL: " + android.os.Build.MODEL);
        Log.d(TAG, "MANUFACTURER: " + android.os.Build.MANUFACTURER);
        Log.d(TAG, "BRAND: " + android.os.Build.BRAND);
        Log.d(TAG, "DEVICE: " + android.os.Build.DEVICE);
        Log.d(TAG, "PRODUCT: " + android.os.Build.PRODUCT);
        Log.d(TAG, "Is Emulator: " + isEmulator);
        
        return isEmulator;
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