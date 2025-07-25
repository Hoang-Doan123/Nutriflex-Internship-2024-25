package com.example.service;

import androidx.annotation.NonNull;

import com.example.model.PersonalData;
import com.example.network.*;
import retrofit2.*;

public class PersonalDataService {
    private final ApiService apiService;

    public PersonalDataService() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public interface PersonalDataCallback {
        void onSuccess(PersonalData data);
        void onError(String error);
    }

    public void getPersonalData(String userId, PersonalDataCallback callback) {
        apiService.getPersonalData(userId).enqueue(new Callback<PersonalData>() {
            @Override
            public void onResponse(@NonNull Call<PersonalData> call, @NonNull Response<PersonalData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch personal data");
                }
            }
            @Override
            public void onFailure(@NonNull Call<PersonalData> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
} 