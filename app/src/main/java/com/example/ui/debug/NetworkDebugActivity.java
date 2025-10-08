package com.example.ui.debug;

import static android.os.Build.*;

import android.annotation.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import android.text.*;

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

    @SuppressLint("SetTextI18n")
    private void updateDisplay() {
        // Initialize ApiClient if not already done
        ApiClient.init(this);
        
        String currentUrl = NetworkConfig.getBaseUrl(this);
        tvCurrentUrl.setText("Current Base URL: " + currentUrl);

        // Device info
        String deviceInfo = "Device Information:\n" +
                "FINGERPRINT: " + FINGERPRINT + "\n" +
                "MODEL: " + MODEL + "\n" +
                "MANUFACTURER: " + MANUFACTURER + "\n" +
                "BRAND: " + BRAND + "\n" +
                "DEVICE: " + DEVICE + "\n" +
                "PRODUCT: " + PRODUCT + "\n";
        
        tvDeviceInfo.setText(deviceInfo);
    }

    private void setupClickListeners() {
        btnTestConnection.setOnClickListener(v -> testConnection());
        btnResetToAuto.setOnClickListener(v -> resetToAuto());
        btnSetEmulator.setOnClickListener(v -> setEmulatorUrl());
        btnSetRealDevice.setOnClickListener(v -> setRealDeviceUrl());
    }

    @SuppressLint("SetTextI18n")
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
        Toast.makeText(this, "Set to emulator URL (10.0.2.2:8080)", Toast.LENGTH_SHORT).show();
    }

    private void setRealDeviceUrl() {
        // Prompt for LAN IP (e.g., 192.168.1.7) and set http://<ip>:8080/
        final EditText input = new EditText(this);
        input.setHint("192.168.1.7");
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Set LAN IP of PC")
                .setMessage("Enter your PC's Wi‑Fi IP (e.g., 192.168.1.7)")
                .setView(input)
                .setPositiveButton("Apply", (dialog, which) -> {
                    String ip = input.getText().toString().trim();
                    if (isValidIPv4(ip)) {
                        String url = "http://" + ip + ":8080/";
                        NetworkConfig.setBaseUrl(this, url);
                        ApiClient.setBaseUrl(url);
                        updateDisplay();
                        Toast.makeText(this, "Set to LAN: " + url, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Invalid IP address", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (d, w) -> {})
                .show();
    }

    private boolean isValidIPv4(String ip) {
        if (ip == null) return false;
        String ipv4 = "^(25[0-5]|2[0-4]\\d|1?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)){3}$";
        return ip.matches(ipv4);
    }
} 