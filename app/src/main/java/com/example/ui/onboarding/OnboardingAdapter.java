package com.example.ui.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.onboarding.OnboardingQuestion;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final List<OnboardingQuestion> questions;
    private final OnOptionSelectedListener optionSelectedListener;
    private final Map<Integer, List<String>> selectedOptions;

    public interface OnOptionSelectedListener {
        void onOptionSelected(int questionPosition, String selectedOption, boolean isSelected);
    }

    public OnboardingAdapter(Context context, OnOptionSelectedListener listener) {
        this.optionSelectedListener = listener;
        this.selectedOptions = new HashMap<>();
        questions = Arrays.asList(
                // Question 1: Gender
                new OnboardingQuestion(
                        context.getString(R.string.onboarding_gender),
                        "",
                        R.drawable.ic_gender,
                        Arrays.asList(
                                context.getString(R.string.onboarding_male),
                                context.getString(R.string.onboarding_female)
                        ),
                        false,
                        OnboardingQuestion.QuestionType.GENDER
                ),
                // Question 2: Age (Input)
                new OnboardingQuestion(
                        context.getString(R.string.onboarding_age),
                        "",
                        R.drawable.ic_age,
                        OnboardingQuestion.QuestionType.INPUT
                ),
                // Question 3: Weight (Input)
                new OnboardingQuestion(
                        context.getString(R.string.onboarding_weight),
                        "",
                        R.drawable.ic_weight,
                        OnboardingQuestion.QuestionType.INPUT
                ),
                // Question 4: Height (Input)
                new OnboardingQuestion(
                        context.getString(R.string.onboarding_height),
                        "",
                        R.drawable.ic_height,
                        OnboardingQuestion.QuestionType.INPUT
                ),
                // Question 5: Body Goal (Single Choice)
                new OnboardingQuestion(
                        context.getString(R.string.onboarding_body_goal),
                        "",
                        R.drawable.ic_goal,
                        Arrays.asList(
                                context.getString(R.string.onboarding_lose_weight),
                                context.getString(R.string.onboarding_gain_weight),
                                context.getString(R.string.onboarding_maintain)
                        ),
                        false,
                        OnboardingQuestion.QuestionType.BODY_GOAL
                ),
                // Question 6: Motivation
                new OnboardingQuestion(
                        context.getString(R.string.onboarding_motivation),
                        "",
                        R.drawable.ic_goal,
                        Arrays.asList(
                                context.getString(R.string.onboarding_motivation_health),
                                context.getString(R.string.onboarding_motivation_immune),
                                context.getString(R.string.onboarding_motivation_look),
                                context.getString(R.string.onboarding_motivation_strength),
                                context.getString(R.string.onboarding_motivation_libido)
                        ),
                        false,
                        OnboardingQuestion.QuestionType.MOTIVATION
                ),
                // Question 7: Healthcare Issues
                new OnboardingQuestion(
                        context.getString(R.string.healthcare_issues),
                        "",
                        R.drawable.ic_healthcare,
                        Arrays.asList(
                                context.getString(R.string.healthcare_diabetes),
                                context.getString(R.string.healthcare_heart),
                                context.getString(R.string.healthcare_blood_pressure),
                                context.getString(R.string.healthcare_sleep),
                                context.getString(R.string.healthcare_none)
                        ),
                        true,
                        OnboardingQuestion.QuestionType.HEALTHCARE
                ),
                // Question 8: Injuries
                new OnboardingQuestion(
                        context.getString(R.string.injuries_title),
                        "",
                        R.drawable.ic_injury,
                        Arrays.asList(
                                context.getString(R.string.injuries_shoulder),
                                context.getString(R.string.injuries_back),
                                context.getString(R.string.injuries_waist),
                                context.getString(R.string.injuries_wrist),
                                context.getString(R.string.injuries_knee),
                                context.getString(R.string.injuries_ankle),
                                context.getString(R.string.injuries_neck),
                                context.getString(R.string.injuries_none)
                        ),
                        true,
                        OnboardingQuestion.QuestionType.INJURIES
                ),
                // Question 9: Dietary Restrictions
                new OnboardingQuestion(
                        context.getString(R.string.nutrition_title),
                        "",
                        R.drawable.ic_nutrition,
                        Arrays.asList(
                                context.getString(R.string.nutrition_vegetarian),
                                context.getString(R.string.nutrition_allergies),
                                context.getString(R.string.nutrition_medical),
                                context.getString(R.string.nutrition_none)
                        ),
                        true,
                        OnboardingQuestion.QuestionType.DIETARY_RESTRICTIONS
                ),
                // Question 10: Fitness Experience
                new OnboardingQuestion(
                        context.getString(R.string.fitness_experience),
                        "",
                        R.drawable.ic_fitness,
                        Arrays.asList(
                                context.getString(R.string.fitness_beginner),
                                context.getString(R.string.fitness_intermediate),
                                context.getString(R.string.fitness_advanced)
                        ),
                        false,
                        OnboardingQuestion.QuestionType.FITNESS_EXPERIENCE
                ),
                // Question 11: Workout Type
                new OnboardingQuestion(
                        context.getString(R.string.onboarding_workout_type),
                        "",
                        R.drawable.ic_fitness, // Sử dụng tạm icon fitness, có thể thay bằng ic_workout nếu có
                        Arrays.asList(
                                context.getString(R.string.workout_type_cardio),
                                context.getString(R.string.workout_type_hypertrophy),
                                context.getString(R.string.workout_type_powerlifting)
                        ),
                        false,
                        OnboardingQuestion.QuestionType.WORKOUT_TYPE
                )
        );
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(questions.get(position), position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void updateSelection(int questionPosition, String option, boolean isSelected) {
        if (!selectedOptions.containsKey(questionPosition)) {
            selectedOptions.put(questionPosition, new ArrayList<>());
        }

        List<String> options = selectedOptions.get(questionPosition);
        
        // For single select questions, clear previous selection
        if (questionPosition == 0 || questionPosition == 1 || questionPosition == 5 || questionPosition == 4) { // Gender, Motivation, Fitness Experience, Body Goal
            options.clear();
        }
        
        if (isSelected) {
            if (!options.contains(option)) {
                options.add(option);
            }
        } else {
            options.remove(option);
        }
        
        notifyItemChanged(questionPosition);
    }

    public List<OnboardingQuestion> getQuestions() {
        return questions;
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final ImageView ivImage;
        private final LinearLayout llOptions;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            llOptions = itemView.findViewById(R.id.llOptions);
        }

        public void bind(OnboardingQuestion question, int questionPosition) {
            tvTitle.setText(question.getTitle());
            tvDescription.setText(question.getDescription());
            ivImage.setImageResource(question.getImageResId());

            // Clear previous options
            llOptions.removeAllViews();

            // Hiển thị EditText nếu là câu hỏi nhập liệu
            EditText etInput = itemView.findViewById(R.id.etInput);
            if (question.getQuestionType() == OnboardingQuestion.QuestionType.INPUT) {
                llOptions.setVisibility(View.GONE);
                etInput.setVisibility(View.VISIBLE);
                // Đặt hint phù hợp
                if (question.getTitle().toLowerCase().contains("tuổi") || question.getTitle().toLowerCase().contains("age")) {
                    etInput.setHint(R.string.onboarding_age_hint);
                    etInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                } else if (question.getTitle().toLowerCase().contains("cân nặng") || question.getTitle().toLowerCase().contains("weight")) {
                    etInput.setHint(R.string.onboarding_weight_hint);
                    etInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
                } else if (question.getTitle().toLowerCase().contains("chiều cao") || question.getTitle().toLowerCase().contains("height")) {
                    etInput.setHint(R.string.onboarding_height_hint);
                    etInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
                // Gán giá trị nếu đã nhập trước đó
                etInput.setText(question.getInputValue() != null ? question.getInputValue() : "");
                // Lắng nghe thay đổi
                etInput.addTextChangedListener(new android.text.TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        question.setInputValue(s.toString());
                    }
                    @Override
                    public void afterTextChanged(android.text.Editable s) {}
                });
            } else {
                etInput.setVisibility(View.GONE);
                llOptions.setVisibility(View.VISIBLE);
                // Get selected options for this question
                List<String> selectedForQuestion = selectedOptions.getOrDefault(questionPosition, new ArrayList<>());
                // Add options
                for (String option : question.getOptions()) {
                    View optionView = LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.item_option, llOptions, false);
                    TextView tvOption = optionView.findViewById(R.id.tvOption);
                    MaterialCardView cardView = (MaterialCardView) optionView;
                    tvOption.setText(option);
                    // Check if this option is selected
                    boolean isSelected = selectedForQuestion.contains(option);
                    updateOptionAppearance(cardView, isSelected);
                    optionView.setOnClickListener(v -> {
                        boolean newSelectionState = !isSelected;
                        updateOptionAppearance(cardView, newSelectionState);
                        if (optionSelectedListener != null) {
                            optionSelectedListener.onOptionSelected(questionPosition, option, newSelectionState);
                        }
                        // Update internal state
                        updateSelection(questionPosition, option, newSelectionState);
                    });
                    llOptions.addView(optionView);
                }
            }
        }

        private void updateOptionAppearance(MaterialCardView cardView, boolean isSelected) {
            if (isSelected) {
                cardView.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.primary));
                cardView.setStrokeColor(ContextCompat.getColorStateList(itemView.getContext(), R.color.primary));
            } else {
                cardView.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.transparent));
                cardView.setStrokeColor(ContextCompat.getColorStateList(itemView.getContext(), R.color.primary));
            }
        }
    }
}
