package com.example.network;

public class KcalRecord {
    private long id;
    private String userId;
    private double distance;
    private int duration;
    private double weight;
    private double kcal;
    private String route;
    private String createdAt;

    public long getId() { return id; }
    public String getUserId() { return userId; }
    public double getDistance() { return distance; }
    public int getDuration() { return duration; }
    public double getWeight() { return weight; }
    public double getKcal() { return kcal; }
    public String getRoute() { return route; }
    public String getCreatedAt() { return createdAt; }
}
