package com.example.ui.me;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.*;
import android.widget.*;

import androidx.annotation.*;

import com.example.R;
import com.example.model.auth.User;
import com.example.service.UserService;
import com.example.utils.SessionManager;

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
    private TextView tvBmiValue;
    private TextView tvBmiCategory;

    private SessionManager sessionManager;
    private UserService userService;

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
        tvBmiValue = view.findViewById(R.id.tvBmiValue);
        tvBmiCategory = view.findViewById(R.id.tvBmiCategory);

        sessionManager = new SessionManager(requireContext());
        userService = new UserService(requireContext());

        setupWeightSeekBar();

        String userId = sessionManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            fetchAndPopulateUser(userId);
        }
    }

    private void setupWeightSeekBar() {
        seekBarWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvWeightValue.setText(progress + " kg");
                updateBmiFromFields(progress, getCurrentHeightCm());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ivEditWeight.setOnClickListener(v -> seekBarWeight.requestFocus());
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

        if (tvUsername != null) tvUsername.setText("Nickname: " + safe(user.getName()));
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
    }

    private String safe(String v) { return v == null ? "" : v; }

    private String formatNumber(Double v) {
        if (v == null) return "--";
        if (Math.abs(v - Math.round(v)) < 0.001) {
            return String.valueOf(v.intValue());
        }
        return String.format(java.util.Locale.getDefault(), "%.1f", v);
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
            tvBmiValue.setText("BMI: " + String.format(java.util.Locale.getDefault(), "%.1f", bmi));
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
}