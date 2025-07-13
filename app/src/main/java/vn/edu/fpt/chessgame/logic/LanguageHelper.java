package vn.edu.fpt.chessgame.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;
public class LanguageHelper {
    private static final String PREF_NAME = "AppLanguagePrefs";
    private static final String KEY_LANGUAGE = "language_code";

    public static void setLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    public static String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "vi"); // mặc định tiếng Việt
    }
}


