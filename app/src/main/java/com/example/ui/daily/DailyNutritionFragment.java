package com.example.ui.daily;

import android.annotation.*;
import android.app.*;
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
import com.google.android.material.floatingactionbutton.*;
import com.google.android.material.progressindicator.*;
import com.google.android.material.textfield.*;

import java.lang.reflect.*;
import java.text.*;
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
    private TextView tvTotalCalories, tvMacros, tvMacroPercentages;
    private LinearProgressIndicator progressCalories;
    private FloatingActionButton fabCreateMealPlan;
    private ProgressBar progressBar;
    private TextView tvMidmorningMeal, tvMidmorningCalories, tvMidmorningTime;
    private TextView tvAfternoonMeal, tvAfternoonCalories, tvAfternoonTime;
    private View cardBreakfast, cardMidmorning, cardLunch, cardAfternoon, cardDinner;

    // Services
    private MealPlanService mealPlanService;
    private String userId;
    private MealPlan currentMealPlan;
    private String fetchedGoal = null;
    private List<String> fetchedDietaryRestrictions = null;
    private String selectedMealPatternType = null;

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

    @SuppressLint("SetTextI18n")
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
        tvMacroPercentages = view.findViewById(R.id.tvMacroPercentages);
        progressCalories = view.findViewById(R.id.progressCalories);
        fabCreateMealPlan = view.findViewById(R.id.fabCreateMealPlan);
        tvMidmorningMeal = view.findViewById(R.id.tvMidmorningMeal);
        tvMidmorningCalories = view.findViewById(R.id.tvMidmorningCalories);
        tvMidmorningTime = view.findViewById(R.id.tvMidmorningTime);
        tvAfternoonMeal = view.findViewById(R.id.tvAfternoonMeal);
        tvAfternoonCalories = view.findViewById(R.id.tvAfternoonCalories);
        tvAfternoonTime = view.findViewById(R.id.tvAfternoonTime);
        cardBreakfast = view.findViewById(R.id.cardBreakfast);
        cardMidmorning = view.findViewById(R.id.cardMidmorning);
        cardLunch = view.findViewById(R.id.cardLunch);
        cardAfternoon = view.findViewById(R.id.cardAfternoon);
        cardDinner = view.findViewById(R.id.cardDinner);

        // Set up click listeners
        fabCreateMealPlan.setOnClickListener(v -> {
            Log.d(TAG, "FAB clicked - showing nutrition goals dialog");
            showNutritionGoalsDialog();
        });

        // Set default meal times
        tvBreakfastTime.setText("8:00 AM");
        tvMidmorningTime.setText("10:30 AM");
        tvLunchTime.setText("12:00 PM");
        tvAfternoonTime.setText("4:00 PM");
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
        // Call API to take personal data first
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

    @SuppressLint("SetTextI18n")
    private void showNutritionGoalsDialogWithData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_nutrition_goals, null);

        // Bind macro sliders
        SeekBar seekProtein = dialogView.findViewById(R.id.seekProtein);
        SeekBar seekCarb = dialogView.findViewById(R.id.seekCarb);
        SeekBar seekFat = dialogView.findViewById(R.id.seekFat);
        TextView tvProteinPct = dialogView.findViewById(R.id.tvProteinPct);
        TextView tvCarbPct = dialogView.findViewById(R.id.tvCarbPct);
        TextView tvFatPct = dialogView.findViewById(R.id.tvFatPct);
        TextView tvMacroSum = dialogView.findViewById(R.id.tvMacroSum);

        // Set defaults based on goal
        applyDefaultMacrosForGoal(tvProteinPct, tvCarbPct, tvFatPct, seekProtein, seekCarb, seekFat);
        updateMacroSum(tvMacroSum, seekProtein.getProgress(), seekCarb.getProgress(), seekFat.getProgress());

        seekProtein.setOnSeekBarChangeListener(new SimpleSeekListener(progress -> {
            tvProteinPct.setText(String.format(Locale.getDefault(), "Protein: %d%%", progress));
            updateMacroSum(tvMacroSum, progress, seekCarb.getProgress(), seekFat.getProgress());
        }));
        seekCarb.setOnSeekBarChangeListener(new SimpleSeekListener(progress -> {
            tvCarbPct.setText(String.format(Locale.getDefault(), "Carbohydrate: %d%%", progress));
            updateMacroSum(tvMacroSum, seekProtein.getProgress(), progress, seekFat.getProgress());
        }));
        seekFat.setOnSeekBarChangeListener(new SimpleSeekListener(progress -> {
            tvFatPct.setText(String.format(Locale.getDefault(), "Fat: %d%%", progress));
            updateMacroSum(tvMacroSum, seekProtein.getProgress(), seekCarb.getProgress(), progress);
        }));

        // Set personal data into TextView
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
            .setPositiveButton("Generate Plan", null)
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            NutritionGoals goals = extractNutritionGoals(dialogView);
            if (goals != null) {
                generateMealPlan(goals);
                dialog.dismiss();
            } // else keep dialog open for correction
        });
    }

    private void applyDefaultMacrosForGoal(TextView tvProteinPct, TextView tvCarbPct, TextView tvFatPct,
                                           SeekBar seekProtein, SeekBar seekCarb, SeekBar seekFat) {
        String goal = fetchedGoal != null ? fetchedGoal : "general_health";
        int p, c, f;
        switch (goal) {
            case "weight_loss":
                p = 30; c = 40; f = 30; break;
            case "muscle_gain":
                p = 35; c = 45; f = 20; break;
            case "maintenance":
                p = 25; c = 50; f = 25; break;
            default:
                p = 20; c = 55; f = 25; break;
        }
        seekProtein.setProgress(p);
        seekCarb.setProgress(c);
        seekFat.setProgress(f);
        tvProteinPct.setText(String.format(Locale.getDefault(), "Protein: %d%%", p));
        tvCarbPct.setText(String.format(Locale.getDefault(), "Carbohydrate: %d%%", c));
        tvFatPct.setText(String.format(Locale.getDefault(), "Fat: %d%%", f));
    }

    private void updateMacroSum(TextView tvMacroSum, int protein, int carb, int fat) {
        int sum = protein + carb + fat;
        tvMacroSum.setText(String.format(Locale.getDefault(), "Total: %d%%", sum));
        try {
            int color = (sum == 100) ? getResources().getColor(R.color.primary_text) : getResources().getColor(R.color.error);
            tvMacroSum.setTextColor(color);
        } catch (Exception ignored) {}
    }

    private static class SimpleSeekListener implements SeekBar.OnSeekBarChangeListener {
        interface OnChange { void onChanged(int progress); }
        private final OnChange onChange;
        SimpleSeekListener(OnChange onChange) { this.onChange = onChange; }
        @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { onChange.onChanged(progress); }
        @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    private NutritionGoals extractNutritionGoals(View dialogView) {
        Log.d(TAG, "Extracting nutrition goals from dialog");
        try {
            // Extract daily calories
            TextInputEditText etDailyCalories = dialogView.findViewById(R.id.etDailyCalories);
            int dailyCalories = Integer.parseInt(etDailyCalories.getText().toString());
            Log.d(TAG, "Daily calories: " + dailyCalories);

            // Meal plan type take from fetchedGoal
            String mealPlanType = fetchedGoal != null ? fetchedGoal : "general_health";
            Log.d(TAG, "Meal plan type (from personal data): " + mealPlanType);

            // Extract meals per day
            RadioGroup rgMealsPerDay = dialogView.findViewById(R.id.rgMealsPerDay);
            int mealsPerDay = getMealsPerDay(rgMealsPerDay.getCheckedRadioButtonId());
            String mealPatternType = getMealPatternType(rgMealsPerDay.getCheckedRadioButtonId());
            selectedMealPatternType = mealPatternType; // Save the chosen mealPatternType
            Log.d(TAG, "Meals per day: " + mealsPerDay + ", Pattern: " + mealPatternType);

            // Extract include snacks
            CheckBox cbIncludeSnacks = dialogView.findViewById(R.id.cbIncludeSnacks);
            boolean includeSnacks = cbIncludeSnacks.isChecked();
            Log.d(TAG, "Include snacks: " + includeSnacks);

            // Take dietary restrictions from fetchedDietaryRestrictions
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

            // Read macro percentages from sliders
            SeekBar seekProtein = dialogView.findViewById(R.id.seekProtein);
            SeekBar seekCarb = dialogView.findViewById(R.id.seekCarb);
            SeekBar seekFat = dialogView.findViewById(R.id.seekFat);
            double proteinPercentage = seekProtein.getProgress();
            double carbohydratePercentage = seekCarb.getProgress();
            double fatPercentage = seekFat.getProgress();

            // Validate exact sum = 100%
            double sum = proteinPercentage + carbohydratePercentage + fatPercentage;
            if (Math.abs(sum - 100.0) > 0.001) {
                Toast.makeText(requireContext(), "Please ensure Protein + Carbohydrate + Fat = 100%", Toast.LENGTH_SHORT).show();
                return null;
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
                                        foodPreferences, allergies, mealPlanType, mealsPerDay, includeSnacks, mealPatternType);
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

    private String getMealPatternType(int checkedId) {
        if (checkedId == R.id.rb3Meals) return "3";
        if (checkedId == R.id.rb4Meals) return "4a";
        if (checkedId == R.id.rb4Meals2) return "4b";
        if (checkedId == R.id.rb5Meals) return "5";
        return "3";
    }

    private int getMealsPerDay(int checkedId) {
        int mealsPerDay;
        if (checkedId == R.id.rb3Meals) mealsPerDay = 3;
        else if (checkedId == R.id.rb4Meals) mealsPerDay = 4;
        else if (checkedId == R.id.rb4Meals2) mealsPerDay = 4;
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
                        // Update UI directly with the saved meal plan
                        currentMealPlan = savedMealPlan;
                        updateUIWithMealPlan(savedMealPlan);
                        // No calling loadTodayMealPlan() immediately
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

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUIWithMealPlan(MealPlan mealPlan) {
        Log.d(TAG, "Updating UI with meal plan");
        
        if (mealPlan == null || mealPlan.getMeals() == null) {
            Log.w(TAG, "Meal plan is null or has no meals, showing default");
            showDefaultMealPlan();
            return;
        }

        Log.d(TAG, "Meal plan has " + mealPlan.getMeals().size() + " meals");

        // Define mealPatternType
        String mealPatternType = null;
        
        // Prefer to use selectedMealPatternType if available (from the selected dialog)
        if (selectedMealPatternType != null) {
            mealPatternType = selectedMealPatternType;
            Log.d(TAG, "Using selected meal pattern: " + mealPatternType);
        } else if (mealPlan instanceof MealPlan) {
            // If mealPlan has mealPatternType field, get it (if the backend returns it)
            try {
                Method m = mealPlan.getClass().getMethod("getMealPatternType");
                mealPatternType = (String) m.invoke(mealPlan);
                Log.d(TAG, "Using meal plan pattern from backend: " + mealPatternType);
            } catch (Exception e) {
                mealPatternType = null;
            }
        }
        
        // If not, determine pattern based on actual meal types
        if (mealPatternType == null) {
            mealPatternType = determinePatternFromMealTypes(mealPlan.getMeals());
            Log.d(TAG, "Using pattern determined from meal types: " + mealPatternType);
        }

        // Set visibility based on pattern
        switch (mealPatternType) {
            case "3":
                cardBreakfast.setVisibility(View.VISIBLE);
                cardMidmorning.setVisibility(View.GONE);
                cardLunch.setVisibility(View.VISIBLE);
                cardAfternoon.setVisibility(View.GONE);
                cardDinner.setVisibility(View.VISIBLE);
                break;
            case "4a":
                cardBreakfast.setVisibility(View.VISIBLE);
                cardMidmorning.setVisibility(View.VISIBLE);
                cardLunch.setVisibility(View.VISIBLE);
                cardAfternoon.setVisibility(View.GONE);
                cardDinner.setVisibility(View.VISIBLE);
                break;
            case "4b":
                cardBreakfast.setVisibility(View.VISIBLE);
                cardMidmorning.setVisibility(View.GONE);
                cardLunch.setVisibility(View.VISIBLE);
                cardAfternoon.setVisibility(View.VISIBLE);
                cardDinner.setVisibility(View.VISIBLE);
                break;
            case "5":
                cardBreakfast.setVisibility(View.VISIBLE);
                cardMidmorning.setVisibility(View.VISIBLE);
                cardLunch.setVisibility(View.VISIBLE);
                cardAfternoon.setVisibility(View.VISIBLE);
                cardDinner.setVisibility(View.VISIBLE);
                break;
            default:
                cardBreakfast.setVisibility(View.VISIBLE);
                cardMidmorning.setVisibility(View.GONE);
                cardLunch.setVisibility(View.VISIBLE);
                cardAfternoon.setVisibility(View.GONE);
                cardDinner.setVisibility(View.VISIBLE);
                break;
        }

        // Continue to update meal content as usual
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
                case "midmorning":
                    tvMidmorningMeal.setText(mealNames);
                    tvMidmorningCalories.setText(totalCalories + " calories");
                    tvMidmorningTime.setText(dailyMeal.getTime());
                    break;
                case "lunch":
                    tvLunchMeal.setText(mealNames);
                    tvLunchCalories.setText(totalCalories + " calories");
                    tvLunchTime.setText(dailyMeal.getTime());
                    break;
                case "afternoon":
                    tvAfternoonMeal.setText(mealNames);
                    tvAfternoonCalories.setText(totalCalories + " calories");
                    tvAfternoonTime.setText(dailyMeal.getTime());
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

        // Keep updating nutrition summary
        MealPlan.NutritionSummary summary = mealPlan.getNutritionSummary();
        if (summary != null) {
            Log.d(TAG, "Updating nutrition summary - Total: " + summary.getTotalCalories() + 
                      "/" + summary.getTargetCalories() + " cal");
            
            tvTotalCalories.setText(summary.getTotalCalories() + " / " + summary.getTargetCalories());
            tvMacros.setText(String.format("Protein: %.0fg \nCarbohydrate: %.0fg \nFat: %.0fg",
                                         summary.getTotalProtein(), summary.getTotalCarbohydrates(), summary.getTotalFat()));
            if (tvMacroPercentages != null) {
                tvMacroPercentages.setText(String.format("Protein: %.0f%% \nCarbohydrate: %.0f%% \nFat: %.0f%%",
                        summary.getProteinPercentage(), summary.getCarbohydratePercentage(), summary.getFatPercentage()));
            }
            
            // Update progress bar
            int progress = (int) ((double) summary.getTotalCalories() / summary.getTargetCalories() * 100);
            progressCalories.setProgress(progress);
            Log.d(TAG, "Progress bar set to: " + progress + "%");
        } else {
            Log.w(TAG, "Nutrition summary is null");
        }
        
        // Reset selectedMealPatternType after use
        selectedMealPatternType = null;
        
        Log.d(TAG, "UI update completed successfully");
    }

    @SuppressLint("SetTextI18n")
    private void showDefaultMealPlan() {
        Log.d(TAG, "Showing default meal plan (no meal plan available)");
        
        tvBreakfastMeal.setText("No meal plan yet");
        tvBreakfastCalories.setText("0 calories");
        tvMidmorningMeal.setText("No meal plan yet");
        tvMidmorningCalories.setText("0 calories");
        tvLunchMeal.setText("No meal plan yet");
        tvLunchCalories.setText("0 calories");
        tvAfternoonMeal.setText("No meal plan yet");
        tvAfternoonCalories.setText("0 calories");
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

    private String determinePatternFromMealTypes(List<MealPlan.DailyMeal> meals) {
        if (meals == null || meals.isEmpty()) {
            return "3";
        }
        
        // Collect all meal types
        List<String> mealTypes = new ArrayList<>();
        for (MealPlan.DailyMeal meal : meals) {
            mealTypes.add(meal.getMealType().toLowerCase());
        }
        
        Log.d(TAG, "Meal types found: " + mealTypes);
        
        // Determine pattern based on meal types
        if (mealTypes.contains("breakfast") && mealTypes.contains("lunch") && mealTypes.contains("dinner") && 
            !mealTypes.contains("midmorning") && !mealTypes.contains("afternoon")) {
            return "3"; // Breakfast, Lunch, Dinner
        } else if (mealTypes.contains("breakfast") && mealTypes.contains("midmorning") && 
                   mealTypes.contains("lunch") && mealTypes.contains("dinner") && 
                   !mealTypes.contains("afternoon")) {
            return "4a"; // Breakfast, Mid-morning, Lunch, Dinner
        } else if (mealTypes.contains("breakfast") && mealTypes.contains("lunch") && 
                   mealTypes.contains("afternoon") && mealTypes.contains("dinner") && 
                   !mealTypes.contains("midmorning")) {
            return "4b"; // Breakfast, Lunch, Afternoon, Dinner
        } else if (mealTypes.contains("breakfast") && mealTypes.contains("midmorning") && 
                   mealTypes.contains("lunch") && mealTypes.contains("afternoon") && 
                   mealTypes.contains("dinner")) {
            return "5"; // All 5 meals
        } else {
            // Fallback: determine by count
            int count = mealTypes.size();
            if (count == 3) return "3";
            else if (count == 4) return "4a"; // default to 4a if unclear
            else if (count == 5) return "5";
            else return "3";
        }
    }

    private String getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, "");
        Log.d(TAG, "Retrieved user ID from preferences: " + (TextUtils.isEmpty(userId) ? "EMPTY" : userId));
        return userId;
    }
}