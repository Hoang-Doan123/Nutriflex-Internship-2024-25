package com.example.service;

import com.example.model.PersonalData;
import com.example.network.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalDataService {
    private ApiService api;

    public PersonalDataService() {
        api = ApiClient.getClient().create(ApiService.class);
    }

    public interface PersonalDataCallback {
        void onSuccess(PersonalData data);
        void onError(String error);
    }

    public void getPersonalData(String userId, PersonalDataCallback callback) {
        api.getPersonalData(userId).enqueue(new Callback<PersonalData>() {
            @Override
            public void onResponse(Call<PersonalData> call, Response<PersonalData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch personal data");
                }
            }
            @Override
            public void onFailure(Call<PersonalData> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
} 