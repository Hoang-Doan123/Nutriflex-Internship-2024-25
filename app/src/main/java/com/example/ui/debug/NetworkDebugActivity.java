package com.example.ui.debug;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.network.*;

public class NetworkDebugActivity extends AppCompatActivity {

    private TextView tvCurrentUrl;
    private TextView tvDeviceInfo;
    private Button btnTestConnection;
    private Button btnResetToAuto;
    private Button btnSetEmulator;
    private Button btnSetRealDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_debug);

        initViews();
        updateDisplay();
        setupClickListeners();
    }

    private void initViews() {
        tvCurrentUrl = findViewById(R.id.tvCurrentUrl);
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo);
        btnTestConnection = findViewById(R.id.btnTestConnection);
        btnResetToAuto = findViewById(R.id.btnResetToAuto);
        btnSetEmulator = findViewById(R.id.btnSetEmulator);
        btnSetRealDevice = findViewById(R.id.btnSetRealDevice);
    }

    private void updateDisplay() {
        // Initialize ApiClient if not already done
        ApiClient.init(this);
        
        String currentUrl = NetworkConfig.getBaseUrl(this);
        tvCurrentUrl.setText("Current Base URL: " + currentUrl);

        // Device info
        StringBuilder deviceInfo = new StringBuilder();
        deviceInfo.append("Device Information:\n");
        deviceInfo.append("FINGERPRINT: ").append(android.os.Build.FINGERPRINT).append("\n");
        deviceInfo.append("MODEL: ").append(android.os.Build.MODEL).append("\n");
        deviceInfo.append("MANUFACTURER: ").append(android.os.Build.MANUFACTURER).append("\n");
        deviceInfo.append("BRAND: ").append(android.os.Build.BRAND).append("\n");
        deviceInfo.append("DEVICE: ").append(android.os.Build.DEVICE).append("\n");
        deviceInfo.append("PRODUCT: ").append(android.os.Build.PRODUCT).append("\n");
        
        tvDeviceInfo.setText(deviceInfo.toString());
    }

    private void setupClickListeners() {
        btnTestConnection.setOnClickListener(v -> testConnection());
        btnResetToAuto.setOnClickListener(v -> resetToAuto());
        btnSetEmulator.setOnClickListener(v -> setEmulatorUrl());
        btnSetRealDevice.setOnClickListener(v -> setRealDeviceUrl());
    }

    private void testConnection() {
        btnTestConnection.setEnabled(false);
        btnTestConnection.setText("Testing...");

        // Test the current base URL
        String baseUrl = NetworkConfig.getBaseUrl(this);
        Log.d("NetworkDebug", "Testing connection to: " + baseUrl);

        // Create a simple test request
        try {
            // You can add actual API test here if needed
            Toast.makeText(this, "Testing connection to: " + baseUrl, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("NetworkDebug", "Connection test failed", e);
            Toast.makeText(this, "Connection test failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            btnTestConnection.setEnabled(true);
            btnTestConnection.setText("Test Connection");
        }
    }

    private void resetToAuto() {
        NetworkConfig.resetToAuto(this);
        ApiClient.resetToAuto();
        updateDisplay();
        Toast.makeText(this, "Reset to auto-detect mode", Toast.LENGTH_SHORT).show();
    }

    private void setEmulatorUrl() {
        NetworkConfig.setBaseUrl(this, "http://10.0.2.2:8080/");
        ApiClient.setBaseUrl("http://10.0.2.2:8080/");
        updateDisplay();
        Toast.makeText(this, "Set to emulator URL", Toast.LENGTH_SHORT).show();
    }

    private void setRealDeviceUrl() {
        NetworkConfig.setBaseUrl(this, "http://192.168.88.168:8080/");
        ApiClient.setBaseUrl("http://192.168.88.168:8080/");
        updateDisplay();
        Toast.makeText(this, "Set to real device URL", Toast.LENGTH_SHORT).show();
    }
} 