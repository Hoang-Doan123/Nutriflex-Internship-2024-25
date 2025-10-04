package com.example.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*;

import com.example.R;
import com.example.model.auth.User;
import com.example.network.*;
import com.example.ui.main.MainActivity;
import com.example.ui.onboarding.OnboardingActivity;
import com.example.utils.SessionManager;

import java.util.*;

import retrofit2.*;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailOrUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView btnRegister;
    private TextView btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiClient.init(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmailOrUsername = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            String emailOrUsername = etEmailOrUsername.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (validateInput(emailOrUsername, password)) {
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Map<String, String> loginRequest = new HashMap<>();
                // Send as "email" field since backend checks both email and username
                loginRequest.put("email", emailOrUsername);
                loginRequest.put("password", password);

                apiService.loginUser(loginRequest).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User user = response.body();
                            if (user.getId() == null || user.getId().isEmpty()) {
                                Log.e("SessionDebug", "userId from backend is null!");
                            } else {
                                SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                sessionManager.setUserProfile(
                                    user.getId(),
                                    user.getEmail(),
                                    user.getWeight(),
                                    user.getHeight(),
                                    user.getAge(),
                                    user.getGender()
                                );
                                // Lưu userId vào SharedPreferences để các fragment khác dùng
                                getSharedPreferences("NutriFlexPrefs", MODE_PRIVATE)
                                    .edit().putString("userId", user.getId()).apply();
                                Log.d("SessionDebug", "Saved userId to session and SharedPreferences: " + user.getId());
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        } else {
                            String errorMessage = "Login failed";
                            if (response.code() == 401) {
                                errorMessage = "Email/Username or password is incorrect";
                            } else if (response.code() == 400) {
                                errorMessage = "Please input all information";
                            } else {
                                errorMessage = "Login error: " + response.code();
                            }
                            String finalErrorMessage = errorMessage;
                            runOnUiThread(() ->
                                    Toast.makeText(LoginActivity.this, finalErrorMessage, Toast.LENGTH_LONG).show()
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                });
            }
        });

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, OnboardingActivity.class))
        );

        btnForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private boolean validateInput(String emailOrUsername, String password) {
        if (TextUtils.isEmpty(emailOrUsername)) {
            etEmailOrUsername.setError("Email/Username is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return false;
        }
        return true;
    }
}