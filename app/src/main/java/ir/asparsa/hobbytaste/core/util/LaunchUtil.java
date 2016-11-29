package ir.asparsa.hobbytaste.core.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class LaunchUtil {

    public static <T extends Activity> void launch(@NonNull Context context, Class<T> clazz, int flags) {
        Intent intent = new Intent(context, clazz);
        if (flags != -1) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | flags);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


    public static <T extends Activity> void launch(@NonNull Context context, Class<T> clazz) {
        launch(context, clazz, -1);
    }
}
