package com.example.model.auth;

import com.example.model.PersonalData;
import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("user")
    private User user;
    @SerializedName("personalData")
    private PersonalData personalData;

    public RegisterResponse() {
    }

    public RegisterResponse(User user, PersonalData personalData) {
        this.user = user;
        this.personalData = personalData;
    }

    // Getters
    public User getUser() {
        return user;
    }

    public PersonalData getPersonalData() {
        return personalData;
    }

    // Setters
    public void setUser(User user) {
        this.user = user;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
    }
} 