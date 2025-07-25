package com.example.ui.daily;

import android.os.Bundle;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import android.view.*;
import android.widget.*;

import com.example.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyWorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyWorkoutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DailyWorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyWorkoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyWorkoutFragment newInstance(String param1, String param2) {
        DailyWorkoutFragment fragment = new DailyWorkoutFragment();
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
        return inflater.inflate(R.layout.fragment_daily_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvWorkoutName = view.findViewById(R.id.tvWorkoutName);
        TextView tvWorkoutDuration = view.findViewById(R.id.tvWorkoutDuration);
        TextView tvWorkoutDesc = view.findViewById(R.id.tvWorkoutDesc);
        ImageView ivWorkoutImage = view.findViewById(R.id.ivWorkoutImage);
        Button btnStartWorkout = view.findViewById(R.id.btnStartWorkout);
        TextView tvStreak = view.findViewById(R.id.tvStreak);
        TextView tvTotalWorkouts = view.findViewById(R.id.tvTotalWorkouts);
        TextView tvMotivation = view.findViewById(R.id.tvMotivation);
        View progressWeekly = view.findViewById(R.id.progressWeekly);

        // Set data (có thể lấy từ ViewModel hoặc API)
        tvWorkoutName.setText("Back and Chest");
        tvWorkoutDuration.setText("45 minutes");
        tvWorkoutDesc.setText("A balanced workout for upper body strength.");
        ivWorkoutImage.setImageResource(R.drawable.ic_workout);

        tvStreak.setText("Current Streak: 5 days");
        tvTotalWorkouts.setText("Total Workouts: 12");
        if (progressWeekly instanceof com.google.android.material.progressindicator.LinearProgressIndicator) {
            ((com.google.android.material.progressindicator.LinearProgressIndicator) progressWeekly).setProgress(70);
        }

        tvMotivation.setText("\u201CPush yourself, because no one else is going to do it for you.\u201D");

        btnStartWorkout.setOnClickListener(v -> {
            // TODO: Chuyển sang màn hình workout detail hoặc bắt đầu workout
        });
    }

    private void setupUI(View view) {
        // TODO: Implement workout schedule display
        // TODO: Implement progress tracking
        // TODO: Implement streaks
        // TODO: Implement duration tracking
    }
}