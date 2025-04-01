package com.example;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {
    private static final String PREP_NAME = "settings";
    private static final String THEME_KEY = "theme";

    public static void saveThemePreference(Context context, boolean isDarkMode) {
        SharedPreferences preferences = context.getSharedPreferences(PREP_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(THEME_KEY, isDarkMode).apply();
    }

    public static boolean isDarkModeEnabled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREP_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(THEME_KEY, false);
    }

    public static void applyTheme(Context context) {
        if (isDarkModeEnabled(context)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
