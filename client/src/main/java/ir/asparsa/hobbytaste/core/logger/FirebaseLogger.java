package ir.asparsa.hobbytaste.core.logger;

import com.google.firebase.crash.FirebaseCrash;
import ir.asparsa.android.BuildConfig;

/**
 * @author hadi
 * @since 3/25/2017 AD.
 */
public class FirebaseLogger {
    public static void e(
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            FirebaseCrash.report(tr);
        }
    }
}
