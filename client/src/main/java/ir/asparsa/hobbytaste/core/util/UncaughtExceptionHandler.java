package ir.asparsa.hobbytaste.core.util;

import android.content.Context;
import android.content.Intent;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.ui.activity.CrashReportActivity;

/**
 * @author hadi
 * @since 3/18/2017 AD.
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final boolean DEBUG = false;
    private Thread.UncaughtExceptionHandler oldHandler;
    private Context context;

    public UncaughtExceptionHandler(
            Thread.UncaughtExceptionHandler oldHandler,
            Context context
    ) {
        this.oldHandler = oldHandler;
        this.context = context;
    }

    @Override public void uncaughtException(
            Thread t,
            Throwable e
    ) {
        if (BuildConfig.DEBUG && !DEBUG) {
            debugMode(t, e);
        } else {
            try {
                // Avoid exceptions in crash report activity
                if (!CrashReportActivity.sCrashReportIsRunning) {
                    Intent intent = LaunchUtil.getIntent(context, CrashReportActivity.class);
                    intent.putExtra(CrashReportActivity.BUNDLE_KEY_CRASH_THROWABLE_NAME, e.getClass().getName());
                    intent.putExtra(CrashReportActivity.BUNDLE_KEY_CRASH_MESSAGE, e.getMessage());
                    context.startActivity(intent);
                }
            } catch (Throwable throwable) {
                throwable.initCause(e);
                e = throwable;
            } finally {
                if (oldHandler != null && !DEBUG) {
                    oldHandler.uncaughtException(t, e);
                }
                if (DEBUG) {
                    debugMode(t, e);
                }
            }
        }
    }

    private void debugMode(
            Thread t,
            Throwable e
    ) {
        L.wtf(getClass(), "In thread: " + t.getName(), e);
        System.exit(10);
    }
}
