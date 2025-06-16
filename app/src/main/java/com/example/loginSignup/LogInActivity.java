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

import com.example.ui.main.MainActivity;
import com.example.R;
import com.example.database.DatabaseHelper;

public class LogInActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    Button btnLogin;
    EditText etNameLogin, etPasswordLogin;
    TextView tvGoToSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        dbHelper = new DatabaseHelper(this);
        etNameLogin = findViewById(R.id.et_name_login);
        etPasswordLogin = findViewById(R.id.et_password_login);
        tvGoToSignup = findViewById(R.id.tv_go_to_signup);
        btnLogin = findViewById(R.id.btn_login);

        tvGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLoggedId = dbHelper.checkUser(etNameLogin.getText().toString(), etPasswordLogin.getText().toString());
                if (isLoggedId) {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(LogInActivity.this, "Incorrect Username or Password", Toast.LENGTH_LONG).show();
            }
        });
    }
}