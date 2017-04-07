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
    private Context context;
    private UncaughtExceptionController controller;

    public UncaughtExceptionHandler(
            UncaughtExceptionController controller,
            Context context
    ) {
        this.controller = controller;
        this.context = context;
    }

    @Override public void uncaughtException(
            Thread t,
            Throwable e
    ) {
        if (controller.isAppDebug() && !controller.isDebug()) {
            controller.debugMode(t, e);
        } else {
            try {
                // Avoid exceptions in crash report activity
                if (!controller.isCrashReportActivityIsRunning()) {
                    controller.launchCrashReportActivity(context, e);
                }
            } catch (Throwable throwable) {
                throwable.initCause(e);
                e = throwable;
            } finally {
                controller.uncaughtException(t, e);
                if (controller.isDebug()) {
                    controller.debugMode(t, e);
                }
            }
        }
    }

    public static class UncaughtExceptionController {

        Thread.UncaughtExceptionHandler oldHandler;

        public UncaughtExceptionController(Thread.UncaughtExceptionHandler oldHandler) {
            this.oldHandler = oldHandler;
        }

        boolean isDebug() {
            return false;
        }

        boolean isAppDebug() {
            return BuildConfig.DEBUG;
        }

        boolean isCrashReportActivityIsRunning() {
            return CrashReportActivity.sCrashReportIsRunning;
        }

        void launchCrashReportActivity(
                Context context,
                Throwable e
        ) {
            Intent intent = LaunchUtil.getIntent(context, CrashReportActivity.class);
            intent.putExtra(CrashReportActivity.BUNDLE_KEY_CRASH_THROWABLE_NAME, e.getClass().getName());
            intent.putExtra(CrashReportActivity.BUNDLE_KEY_CRASH_MESSAGE, e.getMessage());
            context.startActivity(intent);
        }

        void debugMode(
                Thread t,
                Throwable e
        ) {
            L.wtf(getClass(), "In thread: " + t.getName(), e);
//        System.exit(10);
        }

        void uncaughtException(
                Thread t,
                Throwable e
        ) {
            if (oldHandler != null && !isDebug()) {
                oldHandler.uncaughtException(t, e);
            }
        }
    }
}
