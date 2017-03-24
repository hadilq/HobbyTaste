package ir.asparsa.android.core.logger;

import android.util.Log;
import ir.asparsa.android.BuildConfig;

/**
 * @author hadi
 * @since 7/4/2016 AD
 */
public class L {

    public static void e(
            Class<?> clazz,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.e(clazz.getSimpleName(), msg);
        }
    }

    public static void e(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {

        if (BuildConfig.DEBUG) {
            Log.e(clazz.getSimpleName(), msg, tr);
        }
    }

    public static void d(
            Class<?> clazz,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.d(clazz.getSimpleName(), msg);
        }
    }

    public static void d(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.d(clazz.getSimpleName(), msg, tr);
        }
    }

    public static void v(
            Class<?> clazz,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.v(clazz.getSimpleName(), msg);
        }
    }

    public static void v(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.v(clazz.getSimpleName(), msg, tr);
        }
    }

    public static void i(
            Class<?> clazz,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.i(clazz.getSimpleName(), msg);
        }
    }

    public static void i(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.i(clazz.getSimpleName(), msg, tr);
        }
    }

    public static void w(
            Class<?> clazz,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.w(clazz.getSimpleName(), msg);
        }
    }

    public static void w(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.w(clazz.getSimpleName(), msg, tr);
        }
    }

    public static void w(
            Class<?> clazz,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.w(clazz.getSimpleName(), tr);
        }
    }

    public static void wtf(
            Class<?> clazz,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.wtf(clazz.getSimpleName(), tr);
        }
    }

    public static void wtf(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.wtf(clazz.getSimpleName(), msg, tr);
        }
    }

    public static void e(
            String tag,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(
            String tag,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, tr);
        }
    }

    public static void d(
            String tag,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(
            String tag,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg, tr);
        }
    }

    public static void v(
            String tag,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void v(
            String tag,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg, tr);
        }
    }

    public static void i(
            String tag,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void i(
            String tag,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(
            String tag,
            String msg
    ) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void w(
            String tag,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg, tr);
        }
    }

    public static void w(
            String tag,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, tr);
        }
    }

    public static void wtf(
            String tag,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, tr);
        }
    }

    public static void wtf(
            String tag,
            String msg,
            Throwable tr
    ) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, msg, tr);
        }
    }

    public static void fullStackTrace(Throwable e) {
        do {
            StackTraceElement[] elements = e.getStackTrace();

            for (StackTraceElement element : elements) {
                System.out.println(element);
            }
            e = e.getCause();
            if (e != null) {
                System.out.println("Caused by: " + e.getMessage());
            }
        } while (e != null);
    }
}
