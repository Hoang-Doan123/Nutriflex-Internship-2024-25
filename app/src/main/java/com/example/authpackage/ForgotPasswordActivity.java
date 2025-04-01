package com.example.authpackage;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.example.RetrofitClient;
import com.example.requestpackage.ForgotPasswordRequest;
import com.example.responsepackage.ForgotPasswordResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private TextView message;
    private Button sendEmailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        message = findViewById(R.id.message);
        sendEmailBtn = findViewById(R.id.sendEmailBtn);

        sendEmailBtn.setOnClickListener(e -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (this.isValidEmail(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Email is not valid", Toast.LENGTH_SHORT).show();
            } else {

            }
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void resetPassword(String email) {
        sendEmailBtn.setEnabled(false);

        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(email);
        Call<ForgotPasswordResponse> call = RetrofitClient.getAuthApiService().forgotPassword(forgotPasswordRequest);

        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                sendEmailBtn.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordResponse forgotPasswordResponse = response.body();
                    Toast.makeText(ForgotPasswordActivity.this, forgotPasswordResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after successful request
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                sendEmailBtn.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Send Email Failed", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}