package com.example.ui.me;

import static java.util.Locale.*;

import android.annotation.*;
import android.content.*;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.*;
import android.widget.*;

import androidx.annotation.*;

import com.example.R;
import com.example.model.auth.*;
import com.example.model.*;
import com.example.service.*;
import com.example.ui.auth.*;
import com.example.utils.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment {

    private SeekBar seekBarWeight;
    private TextView tvWeightValue;
    private ImageView ivEditWeight;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvGender;
    private TextView tvAge;
    private TextView tvHeight;
    private TextView tvGoal;
    private TextView tvActivityLevel;
    private TextView tvBmiValue;
    private TextView tvBmiCategory;
    private TextView tvBmrValue;
    private TextView tvTdeeValue;
    private LinearLayout btnLogout;

    private SessionManager sessionManager;
    private UserService userService;
    private PersonalDataService personalDataService;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seekBarWeight = view.findViewById(R.id.seekBarWeight);
        tvWeightValue = view.findViewById(R.id.tvWeightValue);
        ivEditWeight = view.findViewById(R.id.ivEditWeight);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvGender = view.findViewById(R.id.tvGender);
        tvAge = view.findViewById(R.id.tvAge);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvGoal = view.findViewById(R.id.tvGoal);
        tvActivityLevel = view.findViewById(R.id.tvActivityLevel);
        tvBmiValue = view.findViewById(R.id.tvBmiValue);
        tvBmiCategory = view.findViewById(R.id.tvBmiCategory);
        tvBmrValue = view.findViewById(R.id.tvBmrValue);
        tvTdeeValue = view.findViewById(R.id.tvTdeeValue);
        btnLogout = view.findViewById(R.id.logoutButton);

        sessionManager = new SessionManager(requireContext());
        userService = new UserService(requireContext());
        personalDataService = new PersonalDataService();

        setupWeightSeekBar();
        setupLogoutButton();

        String userId = sessionManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            fetchAndPopulateUser(userId);
            fetchAndPopulatePersonalData(userId);
        }
    }

    private void setupWeightSeekBar() {
        seekBarWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvWeightValue.setText(progress + " kg");
                updateBmiFromFields(progress, getCurrentHeightCm());
                calculateAndDisplayBmrTdee();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ivEditWeight.setOnClickListener(v -> seekBarWeight.requestFocus());
    }

    private void setupLogoutButton() {
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Clear session data
                sessionManager.logout();
                
                // Clear SharedPreferences
                requireContext().getSharedPreferences("NutriFlexPrefs", 0).edit().clear().apply();
                
                // Navigate to LoginActivity and clear back stack
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                
                // Finish the current activity if it exists
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        }
    }

    private void fetchAndPopulateUser(String userId) {
        userService.getUserById(userId, new UserService.UserCallback() {
            @Override
            public void onSuccess(User user) {
                requireActivity().runOnUiThread(() -> populateUiWithUser(user));
            }

            @Override
            public void onError(String error) {
                // Optionally show a toast
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void populateUiWithUser(User user) {
        if (user == null) return;

        if (tvUsername != null) tvUsername.setText("Username: " + safe(user.getName()));
        if (tvEmail != null) tvEmail.setText("Email: " + safe(user.getEmail()));
        if (tvGender != null) tvGender.setText("Gender: " + safe(user.getGender()));
        if (tvAge != null) tvAge.setText("Age: " + (user.getAge() != null ? user.getAge() : "--"));

        Double heightCm = user.getHeight();
        if (heightCm != null && heightCm > 0) {
            if (tvHeight != null) tvHeight.setText("Height: " + formatNumber(heightCm) + " cm");
        } else {
            if (tvHeight != null) tvHeight.setText("Height: --");
        }

        Double weightKg = user.getWeight();
        if (weightKg != null && weightKg > 0) {
            int weightInt = (int) Math.round(weightKg);
            seekBarWeight.setProgress(weightInt);
            tvWeightValue.setText(weightInt + " kg");
        }

        updateBmiFromFields(getCurrentWeightKg(), getCurrentHeightCm());
        calculateAndDisplayBmrTdee();
    }

    private void fetchAndPopulatePersonalData(String userId) {
        personalDataService.getPersonalData(userId, new PersonalDataService.PersonalDataCallback() {
            @Override
            public void onSuccess(PersonalData personalData) {
                requireActivity().runOnUiThread(() -> populatePersonalData(personalData));
            }

            @Override
            public void onError(String error) {
                // Optionally show a toast
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void populatePersonalData(PersonalData personalData) {
        if (personalData == null) return;

        if (tvGoal != null) {
            String goal = personalData.getGoal();
            if (goal != null && !goal.isEmpty()) {
                tvGoal.setText("Goal: " + goal);
            } else {
                tvGoal.setText("Goal: --");
            }
        }

        if (tvActivityLevel != null) {
            String activityLevel = personalData.getActivityLevel();
            if (activityLevel != null && !activityLevel.isEmpty()) {
                tvActivityLevel.setText("Activity Level: " + formatActivityLevelDisplay(activityLevel));
            } else {
                tvActivityLevel.setText("Activity Level: --");
            }
        }

        // Recalculate BMR and TDEE with personal data
        calculateAndDisplayBmrTdee();
    }

    private String safe(String v) { return v == null ? "" : v; }

    private String formatNumber(Double v) {
        if (v == null) return "--";
        if (Math.abs(v - Math.round(v)) < 0.001) {
            return String.valueOf(v.intValue());
        }
        return String.format(getDefault(), "%.2f", v);
    }

    private double getCurrentWeightKg() {
        try {
            String text = tvWeightValue.getText().toString(); // e.g., "60 kg"
            if (text.contains(" ")) text = text.split(" ")[0];
            return Double.parseDouble(text);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double getCurrentHeightCm() {
        try {
            String text = tvHeight.getText().toString(); // e.g., "Height: 170 cm"
            if (text.contains(":")) text = text.substring(text.indexOf(":") + 1).trim();
            if (text.endsWith("cm")) text = text.replace("cm", "").trim();
            return Double.parseDouble(text);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateBmiFromFields(double weightKg, double heightCm) {
        if (tvBmiValue == null) return;
        if (weightKg > 0 && heightCm > 0) {
            double heightM = heightCm / 100.0;
            double bmi = weightKg / (heightM * heightM);
            tvBmiValue.setText("BMI: " + String.format(getDefault(), "%.2f", bmi));
            if (tvBmiCategory != null) {
                tvBmiCategory.setText(classifyBmiAdult(bmi));
            }
        } else {
            tvBmiValue.setText("BMI: --");
            if (tvBmiCategory != null) {
                tvBmiCategory.setText("");
            }
        }
    }

    private String classifyBmiAdult(double bmi) {
        // WHO adult BMI categories
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal";
        if (bmi < 30.0) return "Overweight";
        if (bmi < 35.0) return "Obesity class I";
        if (bmi < 40.0) return "Obesity class II";
        return "Obesity class III";
    }

    @SuppressLint("SetTextI18n")
    private void calculateAndDisplayBmrTdee() {
        if (tvBmrValue == null || tvTdeeValue == null) return;

        double weightKg = getCurrentWeightKg();
        double heightCm = getCurrentHeightCm();
        String gender = getCurrentGender();
        Integer age = getCurrentAge();
        String activityLevel = getCurrentActivityLevel();

        if (weightKg > 0 && heightCm > 0 && gender != null && age != null) {
            // Calculate BMR using Mifflin-St Jeor Equation
            double bmr;
            if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("nam")) {
                bmr = 88.362 + 13.397 * weightKg + 4.799 * heightCm + (-5.677) * age;
            } else {
                bmr = 447.593 + 9.247 * weightKg + 3.098 * heightCm + (-4.330) * age;
            }

            // Calculate activity factor
            double activityFactor = getActivityFactor(activityLevel);
            double tdee = bmr * activityFactor;

            tvBmrValue.setText("BMR: " + String.format(getDefault(), "%.0f", bmr) + " kcal/day");
            tvTdeeValue.setText("TDEE: " + String.format(getDefault(), "%.0f", tdee) + " kcal/day");

            // Persist latest TDEE for other screens (e.g., default calories in DailyNutritionFragment)
            try {
                int tdeeInt = (int) Math.round(tdee);
                android.content.SharedPreferences prefs = requireContext().getSharedPreferences("NutriFlexPrefs", android.content.Context.MODE_PRIVATE);
                prefs.edit().putInt("tdee", tdeeInt).apply();
            } catch (Exception ignored) {}
        } else {
            tvBmrValue.setText("BMR: --");
            tvTdeeValue.setText("TDEE: --");
        }
    }

    private double getActivityFactor(String activityLevel) {
        if (activityLevel == null) return 1.55; // default moderate
        
        switch (activityLevel.toLowerCase()) {
            case "sedentary": return 1.2;
            case "light": return 1.375;
            case "moderate": return 1.55;
            case "active": return 1.725;
            case "very_active": return 1.9;
            default: return 1.55; // default moderate
        }
    }

    private String getCurrentGender() {
        try {
            String text = tvGender.getText().toString(); // e.g., "Gender: male"
            if (text.contains(":")) {
                return text.substring(text.indexOf(":") + 1).trim();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getCurrentAge() {
        try {
            String text = tvAge.getText().toString(); // e.g., "Age: 25"
            if (text.contains(":")) {
                String ageStr = text.substring(text.indexOf(":") + 1).trim();
                return Integer.parseInt(ageStr);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getCurrentActivityLevel() {
        try {
            String text = tvActivityLevel.getText().toString(); // e.g., "Activity Level: Moderate (moderate exercise/sports 3–5 days/week)"
            if (text.contains(":")) {
                String displayText = text.substring(text.indexOf(":") + 1).trim();
                // Extract the original lowercase activity level from the display text
                return extractOriginalActivityLevel(displayText);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String formatActivityLevelDisplay(String activityLevel) {
        if (activityLevel == null) return "--";
        
        switch (activityLevel.toLowerCase()) {
            case "sedentary":
                return "Sedentary (little or no exercise)";
            case "light":
                return "Light (light exercise/sports 1–3 days/week)";
            case "moderate":
                return "Moderate (moderate exercise/sports 3–5 days/week)";
            case "active":
                return "Active (hard exercise/sports 6–7 days/week)";
            case "very_active":
                return "Very Active (very hard exercise and physical job)";
            default:
                return activityLevel; // fallback to original value
        }
    }

    private String extractOriginalActivityLevel(String displayText) {
        // Extract the original lowercase activity level from the formatted display text
        if (displayText.toLowerCase().startsWith("sedentary")) {
            return "sedentary";
        } else if (displayText.toLowerCase().startsWith("light")) {
            return "light";
        } else if (displayText.toLowerCase().startsWith("moderate")) {
            return "moderate";
        } else if (displayText.toLowerCase().startsWith("active")) {
            return "active";
        } else if (displayText.toLowerCase().startsWith("very active")) {
            return "very_active";
        }
        return "moderate"; // default fallback
    }
}