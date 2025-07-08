package com.example.ui.kcal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import com.example.R;
import com.example.model.auth.User;
import com.example.service.UserService;
import com.example.ui.kcal.KcalSharedViewModel.WorkoutResult;
import android.util.Log;
import com.example.model.PersonalData;
import com.example.network.ApiClient;
import com.example.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kcal_after, container, false);
        tvGoal = view.findViewById(R.id.tvGoal);
        tvCalorieRecommendation = view.findViewById(R.id.tvCalorieRecommendation);
        tvCalorieBurned = view.findViewById(R.id.tvCalorieBurned);
        tvNutritionAdvice = view.findViewById(R.id.tvNutritionAdvice);
        tvMealSuggestion = view.findViewById(R.id.tvMealSuggestion);
        imgNutrition = view.findViewById(R.id.imgNutrition);
        viewModel = new ViewModelProvider(this).get(KcalViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(KcalSharedViewModel.class);
        userService = new UserService(requireContext());

        sharedViewModel.getWorkoutResult().observe(getViewLifecycleOwner(), workoutResult -> {
            if (workoutResult != null && workoutResult.userId != null && !workoutResult.userId.isEmpty()) {
                userId = workoutResult.userId;
                caloriesBurned = workoutResult.caloriesBurned;
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
            public void onResponse(Call<PersonalData> call, Response<PersonalData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userGoal = response.body().getGoal() != null ? response.body().getGoal() : "Maintain Weight";
                } else {
                    userGoal = "Maintain Weight";
                }
                updateNutritionUI();
            }
            @Override
            public void onFailure(Call<PersonalData> call, Throwable t) {
                userGoal = "Maintain Weight";
                updateNutritionUI();
            }
        });
    }

    private void updateNutritionUI() {
        tvGoal.setText("Goal: " + userGoal);
        tvCalorieBurned.setText("Calories Burned: " + caloriesBurned + " kcal");
        int recommendedCalories = calculateRecommendedCalories(userGoal, caloriesBurned);
        tvCalorieRecommendation.setText("Recommended Intake: " + recommendedCalories + " kcal");
        tvNutritionAdvice.setText(getAdviceForGoal(userGoal));
        tvMealSuggestion.setText(getMealSuggestionForGoal(userGoal));
        // Có thể đổi icon dinh dưỡng theo goal nếu muốn
    }

    private int calculateRecommendedCalories(String goal, int caloriesBurned) {
        switch (goal) {
            case "Lose Weight":
                return (int) (caloriesBurned * 0.8); // Giảm 20%
            case "Gain Weight":
                return (int) (caloriesBurned * 1.2); // Tăng 20%
            case "Maintain Weight":
            default:
                return caloriesBurned;
        }
    }

    private String getAdviceForGoal(String goal) {
        switch (goal) {
            case "Lose Weight":
                return "Ăn nhiều rau xanh, protein nạc, hạn chế tinh bột nhanh và đồ ngọt.";
            case "Gain Weight":
                return "Tăng cường thực phẩm giàu protein, tinh bột tốt, ăn thêm bữa phụ giàu năng lượng.";
            case "Maintain Weight":
            default:
                return "Ăn cân bằng các nhóm chất, duy trì chế độ ăn lành mạnh.";
        }
    }

    private String getMealSuggestionForGoal(String goal) {
        switch (goal) {
            case "Lose Weight":
                return "- Ức gà, salad, trứng luộc, yến mạch, cá hồi áp chảo.";
            case "Gain Weight":
                return "- Cơm, thịt bò, cá hồi, bơ, chuối, sữa chua Hy Lạp.";
            case "Maintain Weight":
            default:
                return "- Cơm, thịt nạc, rau củ, trái cây tươi, sữa ít béo.";
        }
    }
}