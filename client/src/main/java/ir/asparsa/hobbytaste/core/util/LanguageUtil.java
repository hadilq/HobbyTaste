package ir.asparsa.hobbytaste.core.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author hadi
 * @since 7/27/2016 AD
 */
public class LanguageUtil {

    public static final String LANGUAGE_FA = "fa";
    public static final String LANGUAGE_EN = "en";

    private static final String COUNTRY_IR = "IR";
    private static final String COUNTRY_US = "US";

    private static final Object sSync = new Object();

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
        synchronized (sSync) {
            loadDefaultLanguage(preferencesManager);
            String country = COUNTRY_MAP.get(sDefaultLanguage);
            if (sLocale == null) {
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
                sLocale = new Locale(sDefaultLanguage, country);
            }
            return sLocale;
        }
    }

    @NonNull
    public static String getLanguageTitle(@NonNull Resources resources) {
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
        if (TextUtils.isEmpty(sDefaultLanguage) || sLocale == null) {
            sDefaultLanguage = preferencesManager.getString(PreferencesManager.KEY_DEFAULT_LANGUAGE, LANGUAGE_EN);
        }
    }

    public static boolean setDefaultLanguage(
            @NonNull PreferencesManager preferencesManager,
            @Language String language
    ) {
        synchronized (sSync) {
            if (!sDefaultLanguage.equals(language)) {
                sDefaultLanguage = language;
                preferencesManager.put(PreferencesManager.KEY_DEFAULT_LANGUAGE, language);
                sLocale = null;
                return true;
            }
            return false;
        }
    }

    public static void reset() {
        sDefaultLanguage = "";
        sLocale = null;
    }

    public static boolean isRTL() {
        synchronized (sSync) {
            return LANGUAGE_FA.equals(sDefaultLanguage);
        }
    }
}
