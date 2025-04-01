package com.example;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import java.util.Locale;


public class LanguageManager {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private Context context;
    private SharedPreferences preferences;

    public LanguageManager(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void updateResource(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        context.createConfigurationContext(configuration);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, code);
        editor.apply();
    }

    public String getLanguage() {
        return preferences.getString(SELECTED_LANGUAGE, "en");
    }
}