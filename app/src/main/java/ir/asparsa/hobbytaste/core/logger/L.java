package ir.asparsa.hobbytaste.core.logger;

import android.util.Log;

/**
 * @author hadi
 * @since 7/4/2016 AD
 */
public class L {
    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        Log.d(tag, msg, tr);
    }

    public static void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        Log.v(tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        Log.i(tag, msg, tr);
    }


    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        Log.w(tag, msg, tr);
    }

    public static void w(String tag, Throwable tr) {
        Log.w(tag, tr);
    }
}
