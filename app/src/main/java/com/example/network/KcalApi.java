package com.example.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface KcalApi {
    @POST("api/kcal/measure")
    Call<KcalRecord> measureAndSave(@Body KcalRequest req);

    @GET("api/kcal/history/{userId}")
    Call<List<KcalRecord>> getHistory(@Path("userId") long userId);

    @POST("api/nutrition/recommendation")
    Call<CalorieRecommendationResponse> getCalorieRecommendation(@Body CalorieRecommendationRequest req);
}
