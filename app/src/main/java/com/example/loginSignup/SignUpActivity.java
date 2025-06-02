package com.example.loginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.example.database.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText etName, etPassword, etEmail;
    private TextView tvGoToLogin;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);
        btnSignup = findViewById(R.id.btn_signup);
        dbHelper = new DatabaseHelper(this);

        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, password, email;
                name = etName.getText().toString();
                password = etPassword.getText().toString();
                email = etEmail.getText().toString();
                // Check whether data fields empty or not
                if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    if (dbHelper.checkUserName(name)) {
                        Toast.makeText(SignUpActivity.this, "Username Already Exists", Toast.LENGTH_LONG).show();
                        return;
                    }
                    boolean isInserted = dbHelper.insertData(name, password, email);
                    if (isInserted) {
                        Toast.makeText(SignUpActivity.this, "Signup Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(SignUpActivity.this, "Signup Failed", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}