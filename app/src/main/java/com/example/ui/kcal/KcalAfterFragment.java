package com.example.ui.kcal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import com.example.R;
import com.example.network.CalorieRecommendationResponse;
import com.example.ui.kcal.KcalSharedViewModel.WorkoutResult;
import android.util.Log;

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

    private long userId = 0;
    private int caloriesBurned = 0;
    private KcalViewModel viewModel;
    private TextView tvRecommendation;
    private KcalSharedViewModel sharedViewModel;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kcal_after, container, false);
        tvRecommendation = view.findViewById(R.id.tvRecommendation);
        viewModel = new ViewModelProvider(this).get(KcalViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(KcalSharedViewModel.class);

        sharedViewModel.getWorkoutResult().observe(getViewLifecycleOwner(), workoutResult -> {
            if (workoutResult != null && workoutResult.userId > 0) {
                Log.d("KcalDebug", "AfterFragment received: userId=" + workoutResult.userId + ", caloriesBurned=" + workoutResult.caloriesBurned);
                viewModel.fetchCalorieRecommendation(workoutResult.userId, workoutResult.caloriesBurned);
                tvRecommendation.setText("Loading recommendation...");
            } else {
                Log.d("KcalDebug", "AfterFragment: No workout data found");
                tvRecommendation.setText("No workout data found.");
            }
        });

        viewModel.getRecommendationResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                tvRecommendation.setText("Your recommended calorie intake: " + result.getRecommendedCalories() + " kcal");
            } else {
                tvRecommendation.setText("Failed to get recommendation. Please try again.");
            }
        });
        return view;
    }
}