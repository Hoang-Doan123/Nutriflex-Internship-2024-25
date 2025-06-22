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

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (validateInput(name, email, password, confirmPassword)) {
                // Extract onboarding data
                String gender = getSelectedOption(0); // Question 0: Gender
                String motivation = getSelectedOption(1); // Question 1: Motivation
                List<String> healthcareIssues = onboardingData.get(2); // Question 2: Healthcare Issues
                List<String> injuries = onboardingData.get(3); // Question 3: Injuries
                List<String> dietaryRestrictions = onboardingData.get(4); // Question 4: Dietary Restrictions
                String fitnessExperience = getSelectedOption(5); // Question 5: Fitness Experience

                // Create RegisterRequest with all data
                RegisterRequest registerRequest = new RegisterRequest(
                    name, email, password, gender, motivation, 
                    healthcareIssues, injuries, dietaryRestrictions, fitnessExperience
                );

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

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                apiService.registerUser(registerRequest).enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
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
                            runOnUiThread(() ->
                                    Toast.makeText(RegisterActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show()
                            );
                            Log.e("RegisterActivity", "Registration failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        runOnUiThread(() ->
                                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                        Log.e("RegisterActivity", "Error: " + t.getMessage(), t);
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
}