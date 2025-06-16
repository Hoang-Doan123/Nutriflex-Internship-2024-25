package com.example;

import android.app.*;
import android.content.*;
import android.os.*;

import java.util.Arrays;

public class NutriFlexApp extends Application {

    public static final String CHANNEL_WORKOUT_ID = "workout_channel";
    public static final String CHANNEL_NUTRITION_ID = "nutrition_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel workoutChannel = new NotificationChannel(
                    CHANNEL_WORKOUT_ID,
                    "Workout Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            workoutChannel.setDescription("Notifications for workout schedules");

            NotificationChannel nutritionChannel = new NotificationChannel(
                    CHANNEL_NUTRITION_ID,
                    "Nutrition Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            nutritionChannel.setDescription("Notifications for meal schedules");

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannels(Arrays.asList(workoutChannel, nutritionChannel));
        }
    }
}
