package ir.asparsa.android.core.logger;

import android.util.Log;

/**
 * @author hadi
 * @since 7/4/2016 AD
 */
public class L {

    public static void e(
            Class<?> clazz,
            String msg
    ) {
        Log.e(clazz.getSimpleName(), msg);
    }

    public static void e(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        Log.e(clazz.getSimpleName(), msg, tr);
    }

    public static void d(
            Class<?> clazz,
            String msg
    ) {
        Log.d(clazz.getSimpleName(), msg);
    }

    public static void d(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        Log.d(clazz.getSimpleName(), msg, tr);
    }

    public static void v(
            Class<?> clazz,
            String msg
    ) {
        Log.v(clazz.getSimpleName(), msg);
    }

    public static void v(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        Log.v(clazz.getSimpleName(), msg, tr);
    }

    public static void i(
            Class<?> clazz,
            String msg
    ) {
        Log.i(clazz.getSimpleName(), msg);
    }

    public static void i(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        Log.i(clazz.getSimpleName(), msg, tr);
    }

    public static void w(
            Class<?> clazz,
            String msg
    ) {
        Log.w(clazz.getSimpleName(), msg);
    }

    public static void w(
            Class<?> clazz,
            String msg,
            Throwable tr
    ) {
        Log.w(clazz.getSimpleName(), msg, tr);
    }

    public static void w(
            Class<?> clazz,
            Throwable tr
    ) {
        Log.w(clazz.getSimpleName(), tr);
    }


    public static void e(
            String tag,
            String msg
    ) {
        Log.e(tag, msg);
    }

    public static void e(
            String tag,
            String msg,
            Throwable tr
    ) {
        Log.e(tag, msg, tr);
    }

    public static void d(
            String tag,
            String msg
    ) {
        Log.d(tag, msg);
    }

    public static void d(
            String tag,
            String msg,
            Throwable tr
    ) {
        Log.d(tag, msg, tr);
    }

    public static void v(
            String tag,
            String msg
    ) {
        Log.v(tag, msg);
    }

    public static void v(
            String tag,
            String msg,
            Throwable tr
    ) {
        Log.v(tag, msg, tr);
    }

    public static void i(
            String tag,
            String msg
    ) {
        Log.i(tag, msg);
    }

    public static void i(
            String tag,
            String msg,
            Throwable tr
    ) {
        Log.i(tag, msg, tr);
    }

    public static void w(
            String tag,
            String msg
    ) {
        Log.w(tag, msg);
    }

    public static void w(
            String tag,
            String msg,
            Throwable tr
    ) {
        Log.w(tag, msg, tr);
    }

    public static void w(
            String tag,
            Throwable tr
    ) {
        Log.w(tag, tr);
    }

    public static void fullStackTrace(Throwable e) {
        do {
            StackTraceElement[] elements = e.getStackTrace();

            for (StackTraceElement element : elements) {
                System.out.println(element);
            }
            e = e.getCause();
            if (e != null) {
                System.out.println("Caused by:");
            }
        } while (e != null);
    }
}
