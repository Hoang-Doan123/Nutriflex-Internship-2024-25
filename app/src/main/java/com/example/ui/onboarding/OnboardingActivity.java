package com.example.ui.onboarding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*;
import androidx.viewpager2.widget.ViewPager2;

import com.example.R;
import com.example.model.onboarding.OnboardingQuestion;
import com.example.ui.auth.RegisterActivity;

import java.util.*;

public class OnboardingActivity extends AppCompatActivity implements OnboardingAdapter.OnOptionSelectedListener {
    private ViewPager2 viewPager;
    private Button btnNext;
    private Button btnSkip;
    private OnboardingAdapter adapter;
    private Map<Integer, List<String>> selectedOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        selectedOptions = new HashMap<>();

        setupViewPager();
        setupButtons();
    }

    private void setupViewPager() {
        adapter = new OnboardingAdapter(this, this);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateButtons(position);
            }
        });
    }

    private void setupButtons() {
        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                navigateToRegister();
            }
        });

        btnSkip.setOnClickListener(v -> navigateToRegister());
    }

    @SuppressLint("SetTextI18n")
    private void updateButtons(int position) {
        if (position == adapter.getItemCount() - 1) {
            btnNext.setText("Get Started");
        } else {
            btnNext.setText("Next");
        }
    }

    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        // Pass onboarding data to RegisterActivity if needed
        intent.putExtra("onboarding_data", new HashMap<>(selectedOptions));
        // Get input values from adapter
        if (adapter != null) {
            List<OnboardingQuestion> questions = adapter.getQuestions();
            intent.putExtra("age", questions.get(1).getInputValue());
            intent.putExtra("weight", questions.get(2).getInputValue());
            intent.putExtra("height", questions.get(3).getInputValue());
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onOptionSelected(int questionPosition, String selectedOption, boolean isSelected) {
        if (!selectedOptions.containsKey(questionPosition)) {
            selectedOptions.put(questionPosition, new ArrayList<>());
        }

        List<String> options = selectedOptions.get(questionPosition);
        
        // For single select questions, clear previous selection
        if (questionPosition == 0 || questionPosition == 1 || questionPosition == 5) { // Gender, Motivation, Fitness Experience
            options.clear();
        }
        
        if (isSelected) {
            if (!options.contains(selectedOption)) {
                options.add(selectedOption);
            }
        } else {
            options.remove(selectedOption);
        }
    }
}