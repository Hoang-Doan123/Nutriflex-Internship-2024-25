package com.example.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*;

import com.example.R;
import com.example.ui.auth.LoginActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPhone, etHeight, etWeight, etAge;
    private Button btnSave, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etAge = findViewById(R.id.etAge);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String height = etHeight.getText().toString();
            String weight = etWeight.getText().toString();
            String age = etAge.getText().toString();

            if (validateInput(name, email, phone, height, weight, age)) {
                // TODO: Implement profile update logic
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnLogout.setOnClickListener(v -> {
            // TODO: Implement logout logic
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private boolean validateInput(String name, String email, String phone, String height, String weight, String age) {
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }
        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            return false;
        }
        if (height.isEmpty()) {
            etHeight.setError("Height is required");
            return false;
        }
        if (weight.isEmpty()) {
            etWeight.setError("Weight is required");
            return false;
        }
        if (age.isEmpty()) {
            etAge.setError("Age is required");
            return false;
        }
        return true;
    }
}