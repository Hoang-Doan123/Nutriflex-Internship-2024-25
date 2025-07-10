package com.example.ui.daily;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.R;
import com.example.model.*;
import com.example.service.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A fragment for displaying daily nutrition information and creating personalized meal plans.
 */
public class DailyNutritionFragment extends Fragment {

    private static final String TAG = "DailyNutritionFragment";
    private static final String PREF_NAME = "NutriFlexPrefs";
    private static final String KEY_USER_ID = "userId";

    // UI Components
    private TextView tvBreakfastMeal, tvBreakfastCalories, tvBreakfastTime;
    private TextView tvLunchMeal, tvLunchCalories, tvLunchTime;
    private TextView tvDinnerMeal, tvDinnerCalories, tvDinnerTime;
    private TextView tvTotalCalories, tvMacros;
    private LinearProgressIndicator progressCalories;
    private FloatingActionButton fabCreateMealPlan;
    private ProgressBar progressBar;

    // Services
    private MealPlanService mealPlanService;
    private String userId;
    private MealPlan currentMealPlan;
    private String fetchedGoal = null;
    private List<String> fetchedDietaryRestrictions = null;

    public DailyNutritionFragment() {
        // Required empty public constructor
    }

    public static DailyNutritionFragment newInstance() {
        return new DailyNutritionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mealPlanService = new MealPlanService(requireContext());
        userId = getUserId();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_nutrition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI(view);
        loadTodayMealPlan();
    }

    private void setupUI(View view) {
        Log.d(TAG, "Setting up UI components");
        
        // Initialize UI components
        tvBreakfastMeal = view.findViewById(R.id.tvBreakfastMeal);
        tvBreakfastCalories = view.findViewById(R.id.tvBreakfastCalories);
        tvBreakfastTime = view.findViewById(R.id.tvBreakfastTime);
        
        tvLunchMeal = view.findViewById(R.id.tvLunchMeal);
        tvLunchCalories = view.findViewById(R.id.tvLunchCalories);
        tvLunchTime = view.findViewById(R.id.tvLunchTime);
        
        tvDinnerMeal = view.findViewById(R.id.tvDinnerMeal);
        tvDinnerCalories = view.findViewById(R.id.tvDinnerCalories);
        tvDinnerTime = view.findViewById(R.id.tvDinnerTime);
        
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories);
        tvMacros = view.findViewById(R.id.tvMacros);
        progressCalories = view.findViewById(R.id.progressCalories);
        fabCreateMealPlan = view.findViewById(R.id.fabCreateMealPlan);

        // Set up click listeners
        fabCreateMealPlan.setOnClickListener(v -> {
            Log.d(TAG, "FAB clicked - showing nutrition goals dialog");
            showNutritionGoalsDialog();
        });

        // Set default meal times
        tvBreakfastTime.setText("8:00 AM");
        tvLunchTime.setText("1:00 PM");
        tvDinnerTime.setText("7:00 PM");
        
        Log.d(TAG, "UI setup completed successfully");
    }

    private void loadTodayMealPlan() {
        Log.d(TAG, "Loading today's meal plan");
        
        if (TextUtils.isEmpty(userId)) {
            Log.w(TAG, "User ID is empty, cannot load meal plan");
            return;
        }

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.d(TAG, "Loading meal plan for date: " + today + ", User ID: " + userId);
        
        mealPlanService.getMealPlan(userId, today, new MealPlanService.MealPlanCallback() {
            @Override
            public void onSuccess(MealPlan mealPlan) {
                Log.d(TAG, "Meal plan loaded successfully");
                currentMealPlan = mealPlan;
                updateUIWithMealPlan(mealPlan);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load meal plan: " + error);
                // Show default/empty state
                showDefaultMealPlan();
            }
        });
    }

    private void showNutritionGoalsDialog() {
        // Gọi API lấy personal data trước
        PersonalDataService personalDataService = new PersonalDataService();
        personalDataService.getPersonalData(userId, new PersonalDataService.PersonalDataCallback() {
            @Override
            public void onSuccess(PersonalData data) {
                fetchedGoal = data.getGoal();
                fetchedDietaryRestrictions = data.getDietaryRestrictions();
                requireActivity().runOnUiThread(() -> showNutritionGoalsDialogWithData());
            }
            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Cannot get personal data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNutritionGoalsDialogWithData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_nutrition_goals, null);

        // Set dữ liệu cá nhân vào TextView
        TextView tvMealPlanType = dialogView.findViewById(R.id.tvMealPlanType);
        tvMealPlanType.setText(fetchedGoal != null ? fetchedGoal : "N/A");

        TextView tvDietaryRestrictions = dialogView.findViewById(R.id.tvDietaryRestrictions);
        if (fetchedDietaryRestrictions != null && !fetchedDietaryRestrictions.isEmpty()) {
            tvDietaryRestrictions.setText(android.text.TextUtils.join(", ", fetchedDietaryRestrictions));
        } else {
            tvDietaryRestrictions.setText("None");
        }

        TextInputLayout tilAllergies = dialogView.findViewById(R.id.tilAllergies);
        boolean showAllergies = false;
        if (fetchedDietaryRestrictions != null) {
            for (String restriction : fetchedDietaryRestrictions) {
                if (restriction != null && restriction.trim().equalsIgnoreCase("Allergies")) {
                    showAllergies = true;
                    break;
                }
            }
        }
        tilAllergies.setVisibility(showAllergies ? View.VISIBLE : View.GONE);

        builder.setView(dialogView)
            .setTitle("Create Personalized Meal Plan")
            .setPositiveButton("Generate Plan", (dialog, which) -> {
                NutritionGoals goals = extractNutritionGoals(dialogView);
                if (goals != null) {
                    generateMealPlan(goals);
                }
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private NutritionGoals extractNutritionGoals(View dialogView) {
        Log.d(TAG, "Extracting nutrition goals from dialog");
        try {
            // Extract daily calories
            TextInputEditText etDailyCalories = dialogView.findViewById(R.id.etDailyCalories);
            int dailyCalories = Integer.parseInt(etDailyCalories.getText().toString());
            Log.d(TAG, "Daily calories: " + dailyCalories);

            // Meal plan type lấy từ fetchedGoal
            String mealPlanType = fetchedGoal != null ? fetchedGoal : "general_health";
            Log.d(TAG, "Meal plan type (from personal data): " + mealPlanType);

            // Extract meals per day
            RadioGroup rgMealsPerDay = dialogView.findViewById(R.id.rgMealsPerDay);
            int mealsPerDay = getMealsPerDay(rgMealsPerDay.getCheckedRadioButtonId());
            Log.d(TAG, "Meals per day: " + mealsPerDay);

            // Extract include snacks
            CheckBox cbIncludeSnacks = dialogView.findViewById(R.id.cbIncludeSnacks);
            boolean includeSnacks = cbIncludeSnacks.isChecked();
            Log.d(TAG, "Include snacks: " + includeSnacks);

            // Dietary restrictions lấy từ fetchedDietaryRestrictions
            List<String> dietaryRestrictions = fetchedDietaryRestrictions != null ? fetchedDietaryRestrictions : new ArrayList<>();
            Log.d(TAG, "Dietary restrictions (from personal data): " + dietaryRestrictions);

            // Extract food preferences
            TextInputEditText etFoodPreferences = dialogView.findViewById(R.id.etFoodPreferences);
            String foodPreferencesText = etFoodPreferences.getText().toString();
            List<String> foodPreferences = TextUtils.isEmpty(foodPreferencesText) ? 
                new ArrayList<>() : Arrays.asList(foodPreferencesText.split(","));
            Log.d(TAG, "Food preferences: " + foodPreferences);

            // Extract allergies
            TextInputEditText etAllergies = dialogView.findViewById(R.id.etAllergies);
            String allergiesText = etAllergies.getText().toString();
            List<String> allergies = TextUtils.isEmpty(allergiesText) ? 
                new ArrayList<>() : Arrays.asList(allergiesText.split(","));
            Log.d(TAG, "Allergies: " + allergies);

            // Set default macronutrient percentages based on meal plan type
            double proteinPercentage, carbohydratePercentage, fatPercentage;
            switch (mealPlanType) {
                case "weight_loss":
                    proteinPercentage = 30.0;
                    carbohydratePercentage = 40.0;
                    fatPercentage = 30.0;
                    break;
                case "muscle_gain":
                    proteinPercentage = 35.0;
                    carbohydratePercentage = 45.0;
                    fatPercentage = 20.0;
                    break;
                case "maintenance":
                    proteinPercentage = 25.0;
                    carbohydratePercentage = 50.0;
                    fatPercentage = 25.0;
                    break;
                default: // general_health
                    proteinPercentage = 20.0;
                    carbohydratePercentage = 55.0;
                    fatPercentage = 25.0;
                    break;
            }
            Log.d(TAG, "Macronutrient percentages - Protein: " + proteinPercentage + 
                      "%, Carbs: " + carbohydratePercentage + "%, Fat: " + fatPercentage + "%");
            String currentUserId = getUserId();
            if (TextUtils.isEmpty(currentUserId)) {
                Log.e(TAG, "User ID is empty! Cannot create NutritionGoals.");
                Toast.makeText(requireContext(), "User not logged in. Please log in again.", Toast.LENGTH_SHORT).show();
                return null;
            }
            NutritionGoals goals = new NutritionGoals(currentUserId, dailyCalories, proteinPercentage, 
                                        carbohydratePercentage, fatPercentage, dietaryRestrictions, 
                                        foodPreferences, allergies, mealPlanType, mealsPerDay, includeSnacks);
            Log.d(TAG, "Nutrition goals created successfully: " + goals.toString());
            return goals;
        } catch (NumberFormatException e) {
            Log.e(TAG, "NumberFormatException while parsing daily calories", e);
            Toast.makeText(requireContext(), "Please enter valid daily calories", Toast.LENGTH_SHORT).show();
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error while extracting nutrition goals", e);
            Toast.makeText(requireContext(), "Error processing nutrition goals", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

//    private String getMealPlanType(int checkedId) {
//        String mealPlanType;
//        if (checkedId == R.id.rbWeightLoss) mealPlanType = "weight_loss";
//        else if (checkedId == R.id.rbMuscleGain) mealPlanType = "muscle_gain";
//        else if (checkedId == R.id.rbMaintenance) mealPlanType = "maintenance";
//        else mealPlanType = "general_health";
//
//        Log.d(TAG, "Meal plan type selected: " + mealPlanType + " (checkedId: " + checkedId + ")");
//        return mealPlanType;
//    }

    private int getMealsPerDay(int checkedId) {
        int mealsPerDay;
        if (checkedId == R.id.rb3Meals) mealsPerDay = 3;
        else if (checkedId == R.id.rb4Meals) mealsPerDay = 4;
        else if (checkedId == R.id.rb5Meals) mealsPerDay = 5;
        else mealsPerDay = 3;
        
        Log.d(TAG, "Meals per day selected: " + mealsPerDay + " (checkedId: " + checkedId + ")");
        return mealsPerDay;
    }

    private void generateMealPlan(NutritionGoals nutritionGoals) {
        Log.d(TAG, "Starting meal plan generation for user: " + userId);
        Log.d(TAG, "Nutrition goals: " + nutritionGoals.toString());
        
        showLoading(true);
        
        mealPlanService.generateMealPlan(userId, nutritionGoals, new MealPlanService.MealPlanCallback() {
            @Override
            public void onSuccess(MealPlan mealPlan) {
                Log.d(TAG, "Meal plan generated successfully");
                showLoading(false);
                currentMealPlan = mealPlan;
                updateUIWithMealPlan(mealPlan);
                
                // Save the meal plan
                Log.d(TAG, "Saving meal plan to database");
                mealPlanService.saveMealPlan(mealPlan, new MealPlanService.MealPlanCallback() {
                    @Override
                    public void onSuccess(MealPlan savedMealPlan) {
                        Log.d(TAG, "Meal plan saved successfully to database");
                        Toast.makeText(requireContext(), "Meal plan created successfully!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Failed to save meal plan to database: " + error);
                        Toast.makeText(requireContext(), "Plan created but failed to save", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to generate meal plan: " + error);
                showLoading(false);
                Toast.makeText(requireContext(), "Failed to generate meal plan: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUIWithMealPlan(MealPlan mealPlan) {
        Log.d(TAG, "Updating UI with meal plan");
        
        if (mealPlan == null || mealPlan.getMeals() == null) {
            Log.w(TAG, "Meal plan is null or has no meals, showing default");
            showDefaultMealPlan();
            return;
        }

        Log.d(TAG, "Meal plan has " + mealPlan.getMeals().size() + " meals");

        // Update meals
        List<MealPlan.DailyMeal> dailyMeals = mealPlan.getMeals() != null ? mealPlan.getMeals() : java.util.Collections.emptyList();
        for (MealPlan.DailyMeal dailyMeal : dailyMeals) {
            String mealNames = "";
            if (dailyMeal.getMeals() != null && !dailyMeal.getMeals().isEmpty()) {
                List<String> names = new ArrayList<>();
                for (com.example.model.Meal meal : dailyMeal.getMeals()) {
                    names.add(meal.getName());
                }
                mealNames = TextUtils.join(", ", names);
            } else {
                mealNames = "No meals";
            }
            int totalCalories = 0;
            if (dailyMeal.getMeals() != null) {
                for (com.example.model.Meal meal : dailyMeal.getMeals()) {
                    totalCalories += meal.getCalories();
                }
            }
            switch (dailyMeal.getMealType().toLowerCase()) {
                case "breakfast":
                    tvBreakfastMeal.setText(mealNames);
                    tvBreakfastCalories.setText(totalCalories + " calories");
                    tvBreakfastTime.setText(dailyMeal.getTime());
                    break;
                case "lunch":
                    tvLunchMeal.setText(mealNames);
                    tvLunchCalories.setText(totalCalories + " calories");
                    tvLunchTime.setText(dailyMeal.getTime());
                    break;
                case "dinner":
                    tvDinnerMeal.setText(mealNames);
                    tvDinnerCalories.setText(totalCalories + " calories");
                    tvDinnerTime.setText(dailyMeal.getTime());
                    break;
                default:
                    break;
            }
        }

        // Update nutrition summary giữ nguyên
        MealPlan.NutritionSummary summary = mealPlan.getNutritionSummary();
        if (summary != null) {
            Log.d(TAG, "Updating nutrition summary - Total: " + summary.getTotalCalories() + 
                      "/" + summary.getTargetCalories() + " cal");
            
            tvTotalCalories.setText(summary.getTotalCalories() + " / " + summary.getTargetCalories());
            tvMacros.setText(String.format("Protein: %.0fg | Carbs: %.0fg | Fat: %.0fg", 
                                         summary.getTotalProtein(), summary.getTotalCarbohydrates(), summary.getTotalFat()));
            
            // Update progress bar
            int progress = (int) ((double) summary.getTotalCalories() / summary.getTargetCalories() * 100);
            progressCalories.setProgress(progress);
            Log.d(TAG, "Progress bar set to: " + progress + "%");
        } else {
            Log.w(TAG, "Nutrition summary is null");
        }
        
        Log.d(TAG, "UI update completed successfully");
    }

    private void showDefaultMealPlan() {
        Log.d(TAG, "Showing default meal plan (no meal plan available)");
        
        tvBreakfastMeal.setText("No meal plan yet");
        tvBreakfastCalories.setText("0 calories");
        tvLunchMeal.setText("No meal plan yet");
        tvLunchCalories.setText("0 calories");
        tvDinnerMeal.setText("No meal plan yet");
        tvDinnerCalories.setText("0 calories");
        
        tvTotalCalories.setText("0 / 0");
        tvMacros.setText("Protein: 0g | Carbs: 0g | Fat: 0g");
        progressCalories.setProgress(0);
        
        Log.d(TAG, "Default meal plan displayed");
    }

    private void showLoading(boolean show) {
        Log.d(TAG, "Setting loading state: " + show);
        
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        fabCreateMealPlan.setEnabled(!show);
        
        Log.d(TAG, "Loading state updated - FAB enabled: " + !show);
    }

    private String getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, "");
        Log.d(TAG, "Retrieved user ID from preferences: " + (TextUtils.isEmpty(userId) ? "EMPTY" : userId));
        return userId;
    }
}