package com.example.ui.kcal;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.*;
import android.widget.*;
import androidx.lifecycle.ViewModelProvider;
import com.example.R;
import com.example.model.NutritionPlan;
import com.example.service.UserService;
import android.util.Log;
import com.example.model.PersonalData;
import com.example.network.*;
import retrofit2.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KcalAfterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KcalAfterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tvGoal, tvCalorieRecommendation, tvCalorieBurned, tvNutritionAdvice, tvMealSuggestion;
    private ImageView imgNutrition;
    private KcalViewModel viewModel;
    private KcalSharedViewModel sharedViewModel;
    private UserService userService;
    private String userGoal = "Maintain Weight";
    private int caloriesBurned = 0;
    private String userId = "";
    private String sessionId = "";

    public KcalAfterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KcalAfterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KcalAfterFragment newInstance(String param1, String param2) {
        KcalAfterFragment fragment = new KcalAfterFragment();
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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kcal_after, container, false);
        tvGoal = view.findViewById(R.id.tvGoal);
        tvCalorieRecommendation = view.findViewById(R.id.tvCalorieRecommendation);
        tvCalorieBurned = view.findViewById(R.id.tvCalorieBurned);
        tvNutritionAdvice = view.findViewById(R.id.tvNutritionAdvice);
//        tvMealSuggestion = view.findViewById(R.id.tvMealSuggestion);
        imgNutrition = view.findViewById(R.id.imgNutrition);
        viewModel = new ViewModelProvider(this).get(KcalViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(KcalSharedViewModel.class);
        userService = new UserService(requireContext());

        sharedViewModel.getWorkoutResult().observe(getViewLifecycleOwner(), workoutResult -> {
            if (workoutResult != null && workoutResult.userId != null && !workoutResult.userId.isEmpty()) {
                userId = workoutResult.userId;
                caloriesBurned = workoutResult.caloriesBurned;
                // Save sessionId if any
                if (workoutResult.sessionId != null) sessionId = workoutResult.sessionId;
                fetchUserGoalAndUpdateUI();
            } else {
                tvGoal.setText("Goal: --");
                tvCalorieRecommendation.setText("Recommended Intake: -- kcal");
                tvCalorieBurned.setText("Calories Burned: -- kcal");
                tvNutritionAdvice.setText("Advice: --");
                tvMealSuggestion.setText("-");
            }
        });
        return view;
    }

    private void fetchUserGoalAndUpdateUI() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getPersonalData(String.valueOf(userId)).enqueue(new Callback<PersonalData>() {
            @Override
            public void onResponse(@NonNull Call<PersonalData> call, @NonNull Response<PersonalData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userGoal = response.body().getGoal() != null ? response.body().getGoal() : "Maintain Weight";
                } else {
                    userGoal = "Maintain Weight";
                }
                updateNutritionUI();
            }
            @Override
            public void onFailure(@NonNull Call<PersonalData> call, @NonNull Throwable t) {
                userGoal = "Maintain Weight";
                updateNutritionUI();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateNutritionUI() {
        tvGoal.setText("Goal: " + userGoal);
        tvCalorieBurned.setText("Calories Burned: " + caloriesBurned + " kcal");
        int recommendedCalories = calculateRecommendedCalories(userGoal, caloriesBurned);
        tvCalorieRecommendation.setText("Recommended Intake: " + recommendedCalories + " kcal");
        tvNutritionAdvice.setText(getAdviceForGoal(userGoal));
//        tvMealSuggestion.setText(getMealSuggestionForGoal(userGoal));
        // Save NutritionPlan into backend
        saveNutritionPlanToBackend(userId, sessionId, recommendedCalories);
    }

    private void saveNutritionPlanToBackend(String userId, String sessionId, int recommendedCalories) {
        if (userId == null || userId.isEmpty() || sessionId == null || sessionId.isEmpty()) {
            Log.e("KcalAfterFragment", "Not saving NutritionPlan: userId or sessionId missing. userId=" + userId + ", sessionId=" + sessionId);
            return;
        }
        Log.d("KcalAfterFragment", "Saving NutritionPlan: userId=" + userId + ", sessionId=" + sessionId + ", recommendedCalories=" + recommendedCalories);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        NutritionPlan plan = new NutritionPlan(userId, sessionId, recommendedCalories, null);
        apiService.saveNutritionPlan(plan).enqueue(new Callback<NutritionPlan>() {
            @Override
            public void onResponse(@NonNull Call<NutritionPlan> call, @NonNull Response<NutritionPlan> response) {
                if (response.isSuccessful()) {
                    Log.d("KcalAfterFragment", "NutritionPlan saved: " + response.body());
                } else {
                    Log.e("KcalAfterFragment", "Failed to save NutritionPlan: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<NutritionPlan> call, @NonNull Throwable t) {
                Log.e("KcalAfterFragment", "Error saving NutritionPlan", t);
            }
        });
    }

    private int calculateRecommendedCalories(String goal, int caloriesBurned) {
        switch (goal) {
            case "Lose Weight":
                return (int) (caloriesBurned * 0.8); // Decrease by 20%
            case "Gain Weight":
                return (int) (caloriesBurned * 1.2); // Increase by 20%
            case "Maintain Weight":
            default:
                return caloriesBurned;
        }
    }

    private String getAdviceForGoal(String goal) {
        switch (goal) {
            case "Lose Weight":
                return "Eat more green vegetables, lean protein, limit fast starches and sweets";
            case "Gain Weight":
                return "Increase foods rich in protein, good starch, and eat extra energy-rich snacks";
            case "Maintain Weight":
            default:
                return "Eat a balanced diet and maintain a healthy diet";
        }
    }

//    private String getMealSuggestionForGoal(String goal) {
//        switch (goal) {
//            case "Lose Weight":
//                return "- Chicken breast, salad, boiled eggs, oatmeal, pan-fried salmon.";
//            case "Gain Weight":
//                return "- Rice, beef, salmon, avocado, banana, Greek yogurt.";
//            case "Maintain Weight":
//            default:
//                return "- Rice, lean meat, vegetables, fresh fruit, low-fat milk.";
//        }
//    }
}