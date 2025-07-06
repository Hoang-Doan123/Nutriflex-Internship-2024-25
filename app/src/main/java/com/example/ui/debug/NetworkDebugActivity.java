package com.example.ui.debug;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.network.*;

import java.util.Map;

import retrofit2.*;

public class NetworkDebugActivity extends AppCompatActivity {
    private static final String TAG = "NetworkDebug";
    
    private TextView tvStatus;
    private TextView tvCurrentUrl;
    private Button btnTestConnection;
    private Button btnChangeUrl;
    private Button btnResetAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_debug);
        
        initViews();
        updateStatus();
        
        btnTestConnection.setOnClickListener(v -> testConnection());
        btnChangeUrl.setOnClickListener(v -> showUrlOptions());
        btnResetAuto.setOnClickListener(v -> resetToAuto());
    }
    
    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvCurrentUrl = findViewById(R.id.tvCurrentUrl);
        btnTestConnection = findViewById(R.id.btnTestConnection);
        btnChangeUrl = findViewById(R.id.btnChangeUrl);
        btnResetAuto = findViewById(R.id.btnResetAuto);
    }
    
    private void updateStatus() {
        String currentUrl = ApiClient.getBaseUrl();
        tvCurrentUrl.setText("Current URL: " + currentUrl);
        tvStatus.setText("Ready to test connection");
    }
    
    private void testConnection() {
        tvStatus.setText("Testing connection...");
        btnTestConnection.setEnabled(false);
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, String>> healthCall = apiService.healthCheck();
        
        healthCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                btnTestConnection.setEnabled(true);
                
                if (response.isSuccessful()) {
                    Map<String, String> result = response.body();
                    String message = "✅ Connection successful!\nStatus: " + response.code() + 
                                   "\nResponse: " + (result != null ? result.toString() : "null");
                    tvStatus.setText(message);
                    Toast.makeText(NetworkDebugActivity.this, "Connection successful!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Health check successful: " + result);
                } else {
                    String message = "❌ Connection failed!\nStatus: " + response.code() + 
                                   "\nError: " + response.message();
                    tvStatus.setText(message);
                    Toast.makeText(NetworkDebugActivity.this, "Connection failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Health check failed: " + response.code() + " - " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                btnTestConnection.setEnabled(true);
                String message = "❌ Connection error!\nError: " + t.getMessage();
                tvStatus.setText(message);
                Toast.makeText(NetworkDebugActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Health check error", t);
            }
        });
    }
    
    private void showUrlOptions() {
        String[] urls = NetworkConfig.getAvailableUrls();
        String[] options = new String[urls.length + 1];
        options[0] = "Enter custom URL";
        System.arraycopy(urls, 0, options, 1, urls.length);
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select URL")
               .setItems(options, (dialog, which) -> {
                   if (which == 0) {
                       showCustomUrlDialog();
                   } else {
                       String selectedUrl = urls[which - 1].split(" ")[0]; // Extract URL part
                       changeUrl(selectedUrl);
                   }
               })
               .show();
    }
    
    private void showCustomUrlDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Enter URL (e.g., http://192.168.1.100:8080/)");
        
        new android.app.AlertDialog.Builder(this)
                .setTitle("Custom URL")
                .setView(input)
                .setPositiveButton("Set", (dialog, which) -> {
                    String url = input.getText().toString().trim();
                    if (!url.isEmpty()) {
                        changeUrl(url);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void changeUrl(String url) {
        if (!url.endsWith("/")) {
            url += "/";
        }
        
        ApiClient.setBaseUrl(url);
        updateStatus();
        Toast.makeText(this, "URL changed to: " + url, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "URL changed to: " + url);
    }
    
    private void resetToAuto() {
        ApiClient.resetToAuto();
        updateStatus();
        Toast.makeText(this, "Reset to auto-detect mode", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Reset to auto-detect mode");
    }
} 