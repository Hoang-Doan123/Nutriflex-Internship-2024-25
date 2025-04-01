package com.example;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import java.util.Locale;

public class LocaleManager {
    private static Locale currentLocale;

    public static Context setLocale(Context context, String language) {
        currentLocale = new Locale(language);
        Locale.setDefault(currentLocale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(currentLocale);

        return context.createConfigurationContext(configuration);
    }

    public static void updateResourcesConfiguration(Context context, String language) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(language));
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}