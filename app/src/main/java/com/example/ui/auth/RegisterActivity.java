package com.example.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.R;
import com.example.model.PersonalData;
import com.example.model.auth.RegisterRequest;
import com.example.model.auth.RegisterResponse;
import com.example.model.auth.User;
import com.example.network.ApiClient;
import com.example.network.ApiService;
import com.example.ui.main.MainActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private TextView btnLogin;
    private Map<Integer, List<String>> onboardingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // Get onboarding data from intent
        Intent intent = getIntent();
        if (intent.hasExtra("onboarding_data")) {
            @SuppressWarnings("unchecked")
            Map<Integer, List<String>> data = (Map<Integer, List<String>>) intent.getSerializableExtra("onboarding_data");
            onboardingData = data != null ? data : new HashMap<>();
        } else {
            onboardingData = new HashMap<>();
        }
        // Nhận thêm age, weight, height
        String ageStr = intent.getStringExtra("age");
        String weightStr = intent.getStringExtra("weight");
        String heightStr = intent.getStringExtra("height");
        Integer age = null;
        Double weight = null, height = null;
        try { if (ageStr != null && !ageStr.isEmpty()) age = Integer.parseInt(ageStr); } catch (Exception ignored) {}
        try { if (weightStr != null && !weightStr.isEmpty()) weight = Double.parseDouble(weightStr); } catch (Exception ignored) {}
        try { if (heightStr != null && !heightStr.isEmpty()) height = Double.parseDouble(heightStr); } catch (Exception ignored) {}

        setupClickListeners(age, weight, height);
    }

    private void setupClickListeners(Integer age, Double weight, Double height) {
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (validateInput(name, email, password, confirmPassword)) {
                // Extract onboarding data
                String gender = getSelectedOption(0); // Question 0: Gender
                String motivation = getSelectedOption(4); // Question 4: Motivation (sau khi thêm 3 input)
                List<String> healthcareIssues = onboardingData.get(5); // Question 5: Healthcare Issues
                List<String> injuries = onboardingData.get(6); // Question 6: Injuries
                List<String> dietaryRestrictions = onboardingData.get(7); // Question 7: Dietary Restrictions
                String fitnessExperience = getSelectedOption(8); // Question 8: Fitness Experience

                // Create RegisterRequest with all data
                RegisterRequest registerRequest = new RegisterRequest(
                    name, email, password, gender, motivation,
                    healthcareIssues, injuries, dietaryRestrictions, fitnessExperience
                );
                registerRequest.setAge(age);
                registerRequest.setWeight(weight);
                registerRequest.setHeight(height);

                // Log the data being sent
                Log.d("RegisterActivity", "Sending registration data:");
                Log.d("RegisterActivity", "Name: " + name);
                Log.d("RegisterActivity", "Email: " + email);
                Log.d("RegisterActivity", "Gender: " + gender);
                Log.d("RegisterActivity", "Motivation: " + motivation);
                Log.d("RegisterActivity", "Healthcare Issues: " + healthcareIssues);
                Log.d("RegisterActivity", "Injuries: " + injuries);
                Log.d("RegisterActivity", "Dietary Restrictions: " + dietaryRestrictions);
                Log.d("RegisterActivity", "Fitness Experience: " + fitnessExperience);
                Log.d("RegisterActivity", "Age: " + age);
                Log.d("RegisterActivity", "Weight: " + weight);
                Log.d("RegisterActivity", "Height: " + height);

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Log.d("RegisterActivity", "API Service created, making request to: " + ApiClient.getBaseUrl());
                // First, test backend connectivity
                apiService.healthCheck().enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        Log.d("RegisterActivity", "Health check response - Code: " + response.code());
                        if (response.isSuccessful()) {
                            Log.d("RegisterActivity", "Backend is accessible, proceeding with registration");
                            // Backend is accessible, proceed with registration
                            performRegistration(apiService, registerRequest);
                        } else {
                            Log.e("RegisterActivity", "Backend health check failed: " + response.code());
                            runOnUiThread(() ->
                                Toast.makeText(RegisterActivity.this, "Backend is not accessible", Toast.LENGTH_LONG).show()
                            );
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Log.e("RegisterActivity", "Health check failed: " + t.getMessage(), t);
                        runOnUiThread(() ->
                            Toast.makeText(RegisterActivity.this, "Cannot connect to backend: " + t.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                });
            }
        });

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private String getSelectedOption(int questionPosition) {
        List<String> options = onboardingData.get(questionPosition);
        if (options != null && !options.isEmpty()) {
            return options.get(0); // Return first selected option for single-select questions
        }
        return null;
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void performRegistration(ApiService apiService, RegisterRequest registerRequest) {
        apiService.registerUser(registerRequest).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d("RegisterActivity", "Response received - Code: " + response.code());
                Log.d("RegisterActivity", "Response headers: " + response.headers());
                
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    User savedUser = registerResponse.getUser();
                    PersonalData savedPersonalData = registerResponse.getPersonalData();
                    
                    Log.d("RegisterActivity", "User registered successfully: " + savedUser.getId());
                    Log.d("RegisterActivity", "Personal data saved: " + savedPersonalData.getId());
                    Log.d("RegisterActivity", "Personal data motivation: " + savedPersonalData.getMotivation());
                    Log.d("RegisterActivity", "Personal data fitness experience: " + savedPersonalData.getFitnessExperience());
                    
                    // Register successfully, move to MainActivity
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    // Process error (example: email exist)
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            Log.e("RegisterActivity", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        errorBody = "Cannot parse error body";
                        Log.e("RegisterActivity", "Error parsing error body: " + e.getMessage());
                    }
                    String errorMsg = "Registration failed: " + response.code() + " - " + errorBody;
                    runOnUiThread(() ->
                            Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show()
                    );
                    Log.e("RegisterActivity", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e("RegisterActivity", "Network failure: " + t.getMessage(), t);
                Log.e("RegisterActivity", "Call URL: " + call.request().url());
                Log.e("RegisterActivity", "Call method: " + call.request().method());
                
                runOnUiThread(() ->
                        Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
                Log.e("RegisterActivity", "Error: " + t.getMessage(), t);
            }
        });
    }
}