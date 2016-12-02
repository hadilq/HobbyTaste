package ir.asparsa.hobbytaste.core.util;

import android.app.Application;
import android.content.res.Configuration;
import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * @author hadi
 * @since 7/27/2016 AD
 */
public class LanguageUtil {

    private static Locale faLocale;

    @NonNull
    public static Locale getLocale() {
        if (faLocale == null) {
            for (Locale locale : Locale.getAvailableLocales()) {
                if (locale.getLanguage().equals("fa") && locale.getCountry().equals("IR")) {
                    faLocale = locale;
                    break;
                }
            }
        }
        if (faLocale == null) {
            faLocale = new Locale("fa");
        }
        return faLocale;
    }

    public static void setupDefaultLocale(@NonNull Application application) {
        Locale.setDefault(getLocale());
        Configuration config = new Configuration();
        config.locale = getLocale();
        application.getBaseContext().getResources().updateConfiguration(
                config,
                application.getBaseContext().getResources().getDisplayMetrics()
        );
    }

    public static boolean isRTL() {
        return isRTL(getLocale());
    }

    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
               directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }
}
