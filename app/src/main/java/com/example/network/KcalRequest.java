package com.example.network;

public class KcalRequest {
    private long userId;
    private double distance;
    private int duration;
    private double weight;
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
