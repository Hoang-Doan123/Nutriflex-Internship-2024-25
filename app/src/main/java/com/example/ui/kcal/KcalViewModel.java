package com.example.ui.kcal;

import android.util.Log;
import androidx.lifecycle.*;

import com.example.network.*;

import java.util.*;

import retrofit2.*;

public class KcalViewModel extends ViewModel {

    private final MutableLiveData<List<KcalRecord>> history = new MutableLiveData<>();
    public LiveData<List<KcalRecord>> getHistory() {
        return history;
    }

    private final MutableLiveData<KcalRecord> measureResult = new MutableLiveData<>();
    public LiveData<KcalRecord> getMeasureResult() {
        return measureResult;
    }

    private final MutableLiveData<CalorieRecommendationResponse> recommendationResult = new MutableLiveData<>();
    public LiveData<CalorieRecommendationResponse> getRecommendationResult() {
        return recommendationResult;
    }

    public void fetchHistory(String userId) {
        Log.d("KcalDebug", "Fetching history for userId: " + userId);
        RetrofitInstance.getApi().getHistory(Long.parseLong(userId)).enqueue(new Callback<List<KcalRecord>>() {
            @Override
            public void onResponse(Call<List<KcalRecord>> call, Response<List<KcalRecord>> response) {
                Log.d("KcalDebug", "History response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("KcalDebug", "History fetched successfully, count: " + response.body().size());
                    history.setValue(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("KcalDebug", "History error: " + response.code() + " - " + errorBody);
                    } catch (Exception e) {
                        Log.e("KcalDebug", "Error reading history error body", e);
                    }
                    history.setValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<KcalRecord>> call, Throwable t) {
                Log.e("KcalDebug", "History network failure", t);
                history.setValue(Collections.emptyList());
            }
        });
    }

    public void measureAndSave(final KcalRequest request) {
        String debugMsg = "Sending request: userId=" + request.getUserId() + 
              ", distance=" + request.getDistance() + ", duration=" + request.getDuration() + 
              ", weight=" + request.getWeight();
        Log.d("KcalDebug", debugMsg);
        
        RetrofitInstance.getApi().measureAndSave(request).enqueue(new Callback<KcalRecord>() {
            @Override
            public void onResponse(Call<KcalRecord> call, Response<KcalRecord> response) {
                String responseMsg = "Server response code: " + response.code();
                Log.d("KcalDebug", responseMsg);
                
                if (response.isSuccessful() && response.body() != null) {
                    KcalRecord result = response.body();
                    String successMsg = "Server success: userId=" + result.getUserId() + 
                          ", kcal=" + result.getKcal() + ", distance=" + result.getDistance();
                    Log.d("KcalDebug", successMsg);
                    measureResult.setValue(result);
                    fetchHistory(request.getUserId());
                } else {
                    String errorMsg = "Server error: " + response.code();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        errorMsg += " - " + errorBody;
                        Log.e("KcalDebug", errorMsg);
                    } catch (Exception e) {
                        Log.e("KcalDebug", "Error reading error body", e);
                    }
                    // Set error message in result for UI display
                    measureResult.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<KcalRecord> call, Throwable t) {
                String failureMsg = "Network failure: " + t.getMessage();
                Log.e("KcalDebug", failureMsg, t);
                measureResult.setValue(null);
            }
        });
    }

    public void fetchCalorieRecommendation(long userId, int caloriesBurned) {
        CalorieRecommendationRequest req = new CalorieRecommendationRequest(userId, caloriesBurned);
        RetrofitInstance.getApi().getCalorieRecommendation(req).enqueue(new Callback<CalorieRecommendationResponse>() {
            @Override
            public void onResponse(Call<CalorieRecommendationResponse> call, Response<CalorieRecommendationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendationResult.setValue(response.body());
                } else {
                    recommendationResult.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CalorieRecommendationResponse> call, Throwable t) {
                recommendationResult.setValue(null);
            }
        });
    }
    
    public void testConnection() {
        Log.d("KcalDebug", "Testing connection to backend...");
        // Try to get history for user 1 as a test
        fetchHistory("1");
    }
}
