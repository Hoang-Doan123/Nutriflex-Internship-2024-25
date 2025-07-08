package com.example.model.onboarding;

import java.util.List;

public class OnboardingQuestion {
    private final String title;
    private final String description;
    private final int imageResId;
    private final List<String> options;
    private final boolean isMultiSelect;
    private final QuestionType questionType;
    private String inputValue;

    public enum QuestionType {
        GENDER,
        MOTIVATION,
        HEALTHCARE,
        INJURIES,
        DIETARY_RESTRICTIONS,
        FITNESS_EXPERIENCE,
        INPUT,
        BODY_GOAL
    }

    public OnboardingQuestion(String title, String description, int imageResId, 
                            List<String> options, boolean isMultiSelect, QuestionType questionType) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.options = options;
        this.isMultiSelect = isMultiSelect;
        this.questionType = questionType;
        this.inputValue = null;
    }

    public OnboardingQuestion(String title, String description, int imageResId, QuestionType questionType) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.options = null;
        this.isMultiSelect = false;
        this.questionType = questionType;
        this.inputValue = null;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }
} 