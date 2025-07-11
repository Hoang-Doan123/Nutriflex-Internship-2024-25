package com.example.ui.alert.meal;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.R;
import android.widget.TextView;
import com.example.model.MealPlan;
import com.example.model.Meal;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.example.service.MealPlanService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertMealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertMealFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MealPlan mealPlan;

    private static final String PREF_NAME = "NutriFlexPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String TAG = "AlertMealFragment";

    private TextView tvBreakfast, tvBreakfastTime, tvMidmorningTitle, tvMidmorning, tvMidmorningTime, tvLunch, tvLunchTime, tvAfternoonTitle, tvAfternoon, tvAfternoonTime, tvDinner, tvDinnerTime;
    private View divider1, divider2;

    public AlertMealFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertMealFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertMealFragment newInstance(String param1, String param2) {
        AlertMealFragment fragment = new AlertMealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nhận meal plan từ arguments
        if (getArguments() != null) {
            mealPlan = (MealPlan) getArguments().getSerializable("meal_plan");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert_meal, container, false);

        tvBreakfast = view.findViewById(R.id.tvBreakfastMeal);
        tvBreakfastTime = view.findViewById(R.id.tvBreakfastTime);
        tvMidmorningTitle = view.findViewById(R.id.tvMidmorningTitle);
        tvMidmorning = view.findViewById(R.id.tvMidmorningMeal);
        tvMidmorningTime = view.findViewById(R.id.tvMidmorningTime);
        tvLunch = view.findViewById(R.id.tvLunchMeal);
        tvLunchTime = view.findViewById(R.id.tvLunchTime);
        tvAfternoonTitle = view.findViewById(R.id.tvAfternoonTitle);
        tvAfternoon = view.findViewById(R.id.tvAfternoonMeal);
        tvAfternoonTime = view.findViewById(R.id.tvAfternoonTime);
        tvDinner = view.findViewById(R.id.tvDinnerMeal);
        tvDinnerTime = view.findViewById(R.id.tvDinnerTime);
        divider1 = view.findViewById(R.id.divider1);
        divider2 = view.findViewById(R.id.divider2);

        if (mealPlan != null && mealPlan.getMeals() != null) {
            displayMealPlan(mealPlan);
        } else {
            // Nếu không truyền mealPlan qua arguments, tự động lấy meal plan gần nhất từ backend
            String userId = getUserId();
            String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
            MealPlanService mealPlanService = new MealPlanService(requireContext());
            mealPlanService.getMealPlan(userId, today, new MealPlanService.MealPlanCallback() {
                @Override
                public void onSuccess(MealPlan latestMealPlan) {
                    Log.d(TAG, "Loaded latest meal plan from backend");
                    displayMealPlan(latestMealPlan);
                }
                @SuppressLint("SetTextI18n")
                @Override
                public void onError(String error) {
                    Log.e(TAG, "No meal plan found, showing default");
                    tvBreakfast.setText("No meal plan yet");
                    tvMidmorning.setText("No meal plan yet");
                    tvLunch.setText("No meal plan yet");
                    tvAfternoon.setText("No meal plan yet");
                    tvDinner.setText("No meal plan yet");
                }
            });
        }

        return view;
    }

    private void displayMealPlan(MealPlan mealPlan) {
        // Xác định mealPatternType
        String mealPatternType = null;
        try {
            java.lang.reflect.Method m = mealPlan.getClass().getMethod("getMealPatternType");
            mealPatternType = (String) m.invoke(mealPlan);
        } catch (Exception e) {
            mealPatternType = null;
        }
        if (mealPatternType == null) {
            int n = mealPlan.getMeals().size();
            if (n == 3) mealPatternType = "3";
            else if (n == 4) mealPatternType = "4a";
            else if (n == 5) mealPatternType = "5";
            else mealPatternType = "3";
        }
        // Set visibility theo pattern
        switch (mealPatternType) {
            case "3":
                tvBreakfast.setVisibility(View.VISIBLE);
                tvBreakfastTime.setVisibility(View.VISIBLE);
                divider1.setVisibility(View.GONE);
                tvMidmorningTitle.setVisibility(View.GONE);
                tvMidmorning.setVisibility(View.GONE);
                tvMidmorningTime.setVisibility(View.GONE);
                tvLunch.setVisibility(View.VISIBLE);
                tvLunchTime.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.GONE);
                tvAfternoonTitle.setVisibility(View.GONE);
                tvAfternoon.setVisibility(View.GONE);
                tvAfternoonTime.setVisibility(View.GONE);
                tvDinner.setVisibility(View.VISIBLE);
                tvDinnerTime.setVisibility(View.VISIBLE);
                break;
            case "4a":
                tvBreakfast.setVisibility(View.VISIBLE);
                tvBreakfastTime.setVisibility(View.VISIBLE);
                divider1.setVisibility(View.VISIBLE);
                tvMidmorningTitle.setVisibility(View.VISIBLE);
                tvMidmorning.setVisibility(View.VISIBLE);
                tvMidmorningTime.setVisibility(View.VISIBLE);
                tvLunch.setVisibility(View.VISIBLE);
                tvLunchTime.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.GONE);
                tvAfternoonTitle.setVisibility(View.GONE);
                tvAfternoon.setVisibility(View.GONE);
                tvAfternoonTime.setVisibility(View.GONE);
                tvDinner.setVisibility(View.VISIBLE);
                tvDinnerTime.setVisibility(View.VISIBLE);
                break;
            case "4b":
                tvBreakfast.setVisibility(View.VISIBLE);
                tvBreakfastTime.setVisibility(View.VISIBLE);
                divider1.setVisibility(View.GONE);
                tvMidmorningTitle.setVisibility(View.GONE);
                tvMidmorning.setVisibility(View.GONE);
                tvMidmorningTime.setVisibility(View.GONE);
                tvLunch.setVisibility(View.VISIBLE);
                tvLunchTime.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.VISIBLE);
                tvAfternoonTitle.setVisibility(View.VISIBLE);
                tvAfternoon.setVisibility(View.VISIBLE);
                tvAfternoonTime.setVisibility(View.VISIBLE);
                tvDinner.setVisibility(View.VISIBLE);
                tvDinnerTime.setVisibility(View.VISIBLE);
                break;
            case "5":
                tvBreakfast.setVisibility(View.VISIBLE);
                tvBreakfastTime.setVisibility(View.VISIBLE);
                divider1.setVisibility(View.VISIBLE);
                tvMidmorningTitle.setVisibility(View.VISIBLE);
                tvMidmorning.setVisibility(View.VISIBLE);
                tvMidmorningTime.setVisibility(View.VISIBLE);
                tvLunch.setVisibility(View.VISIBLE);
                tvLunchTime.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.VISIBLE);
                tvAfternoonTitle.setVisibility(View.VISIBLE);
                tvAfternoon.setVisibility(View.VISIBLE);
                tvAfternoonTime.setVisibility(View.VISIBLE);
                tvDinner.setVisibility(View.VISIBLE);
                tvDinnerTime.setVisibility(View.VISIBLE);
                break;
            default:
                tvBreakfast.setVisibility(View.VISIBLE);
                tvBreakfastTime.setVisibility(View.VISIBLE);
                divider1.setVisibility(View.GONE);
                tvMidmorningTitle.setVisibility(View.GONE);
                tvMidmorning.setVisibility(View.GONE);
                tvMidmorningTime.setVisibility(View.GONE);
                tvLunch.setVisibility(View.VISIBLE);
                tvLunchTime.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.GONE);
                tvAfternoonTitle.setVisibility(View.GONE);
                tvAfternoon.setVisibility(View.GONE);
                tvAfternoonTime.setVisibility(View.GONE);
                tvDinner.setVisibility(View.VISIBLE);
                tvDinnerTime.setVisibility(View.VISIBLE);
                break;
        }
        // Hiển thị nội dung từng meal
        for (MealPlan.DailyMeal dailyMeal : mealPlan.getMeals()) {
            StringBuilder mealNames = new StringBuilder();
            if (dailyMeal.getMeals() != null) {
                for (Meal meal : dailyMeal.getMeals()) {
                    mealNames.append(meal.getName()).append(", ");
                }
            }
            String mealList = mealNames.length() > 0
                    ? mealNames.substring(0, mealNames.length() - 2)
                    : "No meals";
            switch (dailyMeal.getMealType().toLowerCase()) {
                case "breakfast":
                    tvBreakfast.setText(mealList);
                    tvBreakfastTime.setText(dailyMeal.getTime());
                    break;
                case "midmorning":
                    tvMidmorning.setText(mealList);
                    tvMidmorningTime.setText(dailyMeal.getTime());
                    break;
                case "lunch":
                    tvLunch.setText(mealList);
                    tvLunchTime.setText(dailyMeal.getTime());
                    break;
                case "afternoon":
                    tvAfternoon.setText(mealList);
                    tvAfternoonTime.setText(dailyMeal.getTime());
                    break;
                case "dinner":
                    tvDinner.setText(mealList);
                    tvDinnerTime.setText(dailyMeal.getTime());
                    break;
            }
        }
    }

    private String getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, "");
        Log.d(TAG, "Retrieved user ID from preferences: " + (TextUtils.isEmpty(userId) ? "EMPTY" : userId));
        return userId;
    }
}