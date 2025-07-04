package com.example.ui.kcal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.network.KcalRecord;
import com.example.network.KcalRequest;
import com.example.network.RetrofitInstance;
import com.example.network.CalorieRecommendationRequest;
import com.example.network.CalorieRecommendationResponse;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void fetchHistory(long userId) {
        RetrofitInstance.getApi().getHistory(userId).enqueue(new Callback<List<KcalRecord>>() {
            @Override
            public void onResponse(Call<List<KcalRecord>> call, Response<List<KcalRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    history.setValue(response.body());
                } else {
                    history.setValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<KcalRecord>> call, Throwable t) {
                history.setValue(Collections.emptyList());
            }
        });
    }

    public void measureAndSave(final KcalRequest request) {
        RetrofitInstance.getApi().measureAndSave(request).enqueue(new Callback<KcalRecord>() {
            @Override
            public void onResponse(Call<KcalRecord> call, Response<KcalRecord> response) {
                if (response.isSuccessful() && response.body() != null) {
                    measureResult.setValue(response.body());
                    fetchHistory(request.getUserId());
                } else {
                    measureResult.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<KcalRecord> call, Throwable t) {
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
}
