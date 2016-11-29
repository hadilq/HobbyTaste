package ir.asparsa.hobbytaste.core.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.fragment.BaseFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class NavigationUtil {

    public static void startFragment(@NonNull FragmentActivity activity, @NonNull BaseContentFragment fragment) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment, fragment.getFragmentTag())
                .addToBackStack(fragment.getFragmentTag())
                .commit();
    }

    public static void startNestedFragment(@NonNull FragmentManager fragmentManager, @NonNull BaseFragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.content_nested, fragment)
                .commit();
    }

    public static void popBackStack(@NonNull FragmentActivity activity) {
        activity.getSupportFragmentManager().popBackStack();
    }

    public static Fragment findTopFragment(@NonNull FragmentActivity activity) {
        return activity.getSupportFragmentManager().findFragmentById(R.id.content);
    }

    public static Fragment getActiveFragment(FragmentManager fragmentManager) {
        return fragmentManager.findFragmentById(R.id.content_nested);
    }
}