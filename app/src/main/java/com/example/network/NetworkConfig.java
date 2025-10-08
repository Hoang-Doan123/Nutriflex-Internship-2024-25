package com.example.network;

import static android.os.Build.*;

import android.content.*;
import android.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class NetworkConfig {
    private static final String TAG = "NetworkConfig";
    private static final String PREF_NAME = "network_config";
    private static final String KEY_BASE_URL = "base_url";
    private static final String KEY_AUTO_DETECTED_IP = "auto_detected_ip";
    
    // Default URLs
    private static final String EMULATOR_URL = "http://10.0.2.2:8080/";
    private static final String DEVICE_URL = "http://192.168.1.7:8080/"; // Fallback IP
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
            // If running on emulator, always force the emulator URL
            if (isEmulator() && !EMULATOR_URL.equals(savedUrl)) {
                currentBaseUrl = EMULATOR_URL;
                prefs.edit().putString(KEY_BASE_URL, currentBaseUrl).apply();
                Log.d(TAG, "Overriding saved URL for emulator: " + currentBaseUrl);
                return currentBaseUrl;
            }
            // On real device, never allow localhost/127.0.0.1
            if (!isEmulator() && (savedUrl.contains("localhost") || savedUrl.contains("127.0.0.1"))) {
                currentBaseUrl = DEVICE_URL;
                prefs.edit().putString(KEY_BASE_URL, currentBaseUrl).apply();
                Log.d(TAG, "Overriding saved URL for real device (no localhost): " + currentBaseUrl);
                return currentBaseUrl;
            }
            currentBaseUrl = savedUrl;
            Log.d(TAG, "Using saved URL: " + currentBaseUrl);
            return currentBaseUrl;
        }
        
        // Auto-detect: if running on emulator, use emulator URL, otherwise auto-detect LAN IP
        if (isEmulator()) {
            currentBaseUrl = EMULATOR_URL;
            Log.d(TAG, "Detected emulator, using: " + currentBaseUrl);
        } else {
            // Try to auto-detect LAN IP for real device
            String detectedIp = autoDetectLanIp(context);
            if (detectedIp != null) {
                currentBaseUrl = "http://" + detectedIp + ":8080/";
                Log.d(TAG, "Auto-detected LAN IP: " + currentBaseUrl);
            } else {
                currentBaseUrl = DEVICE_URL;
                Log.d(TAG, "Auto-detect failed, using fallback: " + currentBaseUrl);
            }
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
        boolean isEmulator = FINGERPRINT.startsWith("generic")
                || FINGERPRINT.startsWith("unknown")
                || MODEL.contains("google_sdk")
                || MODEL.contains("Emulator")
                || MODEL.contains("Android SDK built for x86")
                || MANUFACTURER.contains("Genymotion")
                || (BRAND.startsWith("generic") && DEVICE.startsWith("generic"))
                || "google_sdk".equals(PRODUCT)
                || MODEL.contains("sdk_gphone")
                || MODEL.contains("Pixel")
                || MODEL.contains("Android SDK built for x86_64");
        
        Log.d(TAG, "Device detection:");
        Log.d(TAG, "FINGERPRINT: " + FINGERPRINT);
        Log.d(TAG, "MODEL: " + MODEL);
        Log.d(TAG, "MANUFACTURER: " + MANUFACTURER);
        Log.d(TAG, "BRAND: " + BRAND);
        Log.d(TAG, "DEVICE: " + DEVICE);
        Log.d(TAG, "PRODUCT: " + PRODUCT);
        Log.d(TAG, "Is Emulator: " + isEmulator);
        
        return isEmulator;
    }
    
    /**
     * Auto-detect LAN IP by scanning common subnets and testing connectivity
     */
    private static String autoDetectLanIp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String cachedIp = prefs.getString(KEY_AUTO_DETECTED_IP, null);
        
        // Check if cached IP is still working
        if (cachedIp != null && testConnectivity("http://" + cachedIp + ":8080/api/users/health")) {
            Log.d(TAG, "Using cached LAN IP: " + cachedIp);
            return cachedIp;
        }
        
        // Scan common LAN subnets
        String[] subnets = {"192.168.1.", "192.168.0.", "192.168.88.", "10.0.0."};
        
        for (String subnet : subnets) {
            Log.d(TAG, "Scanning subnet: " + subnet);
            for (int i = 1; i <= 254; i++) {
                String ip = subnet + i;
                String url = "http://" + ip + ":8080/api/users/health";
                
                if (testConnectivity(url)) {
                    Log.d(TAG, "Found working IP: " + ip);
                    // Cache the working IP
                    prefs.edit().putString(KEY_AUTO_DETECTED_IP, ip).apply();
                    return ip;
                }
            }
        }
        
        Log.d(TAG, "No working LAN IP found");
        return null;
    }
    
    /**
     * Test connectivity to a URL with short timeout
     */
    private static boolean testConnectivity(String url) {
        try {
            URL testUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) testUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000); // 1 second timeout
            connection.setReadTimeout(1000);
            connection.setInstanceFollowRedirects(false);
            
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Force re-detection of LAN IP (call this when IP might have changed)
     */
    public static void forceReDetection(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_AUTO_DETECTED_IP).apply();
        currentBaseUrl = null; // Force re-detection on next getBaseUrl call
        Log.d(TAG, "Forced LAN IP re-detection");
    }
} 