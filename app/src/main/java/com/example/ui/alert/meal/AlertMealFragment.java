package com.example.ui.alert.meal;

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

        // Lấy các TextView để hiển thị món ăn
        TextView tvBreakfast = view.findViewById(R.id.tvBreakfastMeal);
        TextView tvLunch = view.findViewById(R.id.tvLunchMeal);
        TextView tvDinner = view.findViewById(R.id.tvDinnerMeal);

        if (mealPlan != null && mealPlan.getMeals() != null) {
            displayMealPlan(mealPlan, tvBreakfast, tvLunch, tvDinner);
        } else {
            // Nếu không truyền mealPlan qua arguments, tự động lấy meal plan gần nhất từ backend
            String userId = getUserId();
            String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
            MealPlanService mealPlanService = new MealPlanService(requireContext());
            mealPlanService.getMealPlan(userId, today, new MealPlanService.MealPlanCallback() {
                @Override
                public void onSuccess(MealPlan latestMealPlan) {
                    Log.d(TAG, "Loaded latest meal plan from backend");
                    displayMealPlan(latestMealPlan, tvBreakfast, tvLunch, tvDinner);
                }
                @Override
                public void onError(String error) {
                    Log.e(TAG, "No meal plan found, showing default");
                    tvBreakfast.setText("No meal plan yet");
                    tvLunch.setText("No meal plan yet");
                    tvDinner.setText("No meal plan yet");
                }
            });
        }

        return view;
    }

    private void displayMealPlan(MealPlan mealPlan, TextView tvBreakfast, TextView tvLunch, TextView tvDinner) {
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
                    break;
                case "lunch":
                    tvLunch.setText(mealList);
                    break;
                case "dinner":
                    tvDinner.setText(mealList);
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