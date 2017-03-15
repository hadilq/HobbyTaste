package ir.asparsa.hobbytaste.core.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;

/**
 * @author hadi
 * @since 7/27/2016 AD
 */
public class LanguageUtil {

    public static final String LANGUAGE_FA = "fa";
    public static final String LANGUAGE_EN = "en";

    private static final String COUNTRY_IR = "IR";
    private static final String COUNTRY_US = "US";

    private static final Map<String, String> COUNTRY_MAP = new HashMap<String, String>() {{
        put(LANGUAGE_FA, COUNTRY_IR);
        put(LANGUAGE_EN, COUNTRY_US);
    }};


    @StringDef({LANGUAGE_FA, LANGUAGE_EN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Language {
    }

    private static String sDefaultLanguage;
    private static Locale sLocale;

    @NonNull
    public static Locale getLocale(@NonNull PreferencesManager preferencesManager) {
        loadDefaultLanguage(preferencesManager);
        if (sLocale == null) {
            String country = COUNTRY_MAP.get(sDefaultLanguage);

            for (Locale locale : Locale.getAvailableLocales()) {
                if (locale.getLanguage().equals(sDefaultLanguage) && locale.getCountry().equals(country)) {
                    sLocale = locale;
                    break;
                }
            }
        }
        if (sLocale == null) {
            for (Locale locale : Locale.getAvailableLocales()) {
                if (locale.getLanguage().equals(sDefaultLanguage)) {
                    sLocale = locale;
                    break;
                }
            }
        }
        if (sLocale == null) {
            setDefaultLanguage(preferencesManager, LANGUAGE_EN);
            sLocale = Locale.US;
        }
        return sLocale;
    }

    @NonNull
    public static String getLanguage(@NonNull Resources resources) {
        String lang = Locale.getDefault().getLanguage();
        switch (lang) {
            case LANGUAGE_FA:
                return resources.getString(R.string.persian);
            case LANGUAGE_EN:
                return resources.getString(R.string.english);
        }
        return "";
    }

    public static void setupDefaultLocale(
            @NonNull PreferencesManager preferencesManager,
            @NonNull Context context
    ) {
        Locale.setDefault(getLocale(preferencesManager));
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(Locale.getDefault());
        context.createConfigurationContext(config);
        context.getResources().updateConfiguration(config, null);
    }

    private static void loadDefaultLanguage(@NonNull PreferencesManager preferencesManager) {
        sDefaultLanguage = preferencesManager.getString(PreferencesManager.KEY_DEFAULT_LANGUAGE, LANGUAGE_FA);
    }

    public static boolean setDefaultLanguage(
            @NonNull PreferencesManager preferencesManager,
            @Language String language
    ) {
        if (!sDefaultLanguage.equals(language)) {
            sDefaultLanguage = language;
            preferencesManager.put(PreferencesManager.KEY_DEFAULT_LANGUAGE, language);
            sLocale = null;
            return true;
        }
        return false;
    }

    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }


    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
               directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }
}
