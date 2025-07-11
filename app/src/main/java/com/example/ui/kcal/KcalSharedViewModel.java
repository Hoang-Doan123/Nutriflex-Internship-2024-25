package com.example.ui.kcal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KcalSharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> switchToAfterTab = new MutableLiveData<>();
    private final MutableLiveData<WorkoutResult> workoutResult = new MutableLiveData<>();

    public void requestSwitchToAfterTab() {
        switchToAfterTab.setValue(true);
    }

    public LiveData<Boolean> getSwitchToAfterTab() {
        return switchToAfterTab;
    }

    public void setWorkoutResult(String userId, int caloriesBurned, String sessionId) {
        workoutResult.setValue(new WorkoutResult(userId, caloriesBurned, sessionId));
    }

    public LiveData<WorkoutResult> getWorkoutResult() {
        return workoutResult;
    }

    public static class WorkoutResult {
        public final String userId;
        public final int caloriesBurned;
        public final String sessionId;
        public WorkoutResult(String userId, int caloriesBurned, String sessionId) {
            this.userId = userId;
            this.caloriesBurned = caloriesBurned;
            this.sessionId = sessionId;
        }
    }
} 