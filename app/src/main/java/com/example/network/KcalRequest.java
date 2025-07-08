package com.example.network;

import com.google.gson.annotations.SerializedName;

public class KcalRequest {
    @SerializedName("userId")
    private long userId;
    @SerializedName("distance")
    private double distance;
    @SerializedName("duration")
    private int duration;
    @SerializedName("weight")
    private double weight;
    @SerializedName("route")
    private String route;

    public KcalRequest(long userId, double distance, int duration, double weight, String route) {
        this.userId = userId;
        this.distance = distance;
        this.duration = duration;
        this.weight = weight;
        this.route = route;
    }

    public long getUserId() { return userId; }
    public double getDistance() { return distance; }
    public int getDuration() { return duration; }
    public double getWeight() { return weight; }
    public String getRoute() { return route; }
}
