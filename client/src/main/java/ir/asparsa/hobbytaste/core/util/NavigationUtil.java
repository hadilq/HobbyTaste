package ir.asparsa.hobbytaste.core.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import ir.asparsa.android.ui.fragment.BaseFragment;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import junit.framework.Assert;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class NavigationUtil {

    public static void startContentFragment(
            @NonNull FragmentManager fragmentManager,
            @NonNull BaseContentFragment fragment
    ) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment, fragment.getTagName())
                .addToBackStack(fragment.getTagName())
                .commit();
    }

    public static void startNestedFragment(
            @NonNull FragmentManager fragmentManager,
            @NonNull BaseFragment fragment
    ) {
        fragmentManager.beginTransaction()
                       .replace(R.id.content_nested, fragment, fragment.getTagName())
                       .commit();
    }

    public static void popBackStack(@NonNull FragmentManager activeFragmentManager) {
        activeFragmentManager.popBackStack();
    }

    @Nullable
    public static BaseContentFragment findTopFragment(
            @NonNull FragmentManager fragmentManager
    ) {

        Fragment fragment = fragmentManager.findFragmentById(R.id.content);
        if (fragment instanceof BaseContentFragment) {
            return (BaseContentFragment) fragment;
        }
        if (fragment != null) {
            Assert.fail("Fragment is not a content! It's " + fragment.getClass());
        }
        return null;
    }

    public static Fragment getActiveFragment(@NonNull FragmentManager fragmentManager) {
        return fragmentManager.findFragmentById(R.id.content_nested);
    }

    public static BaseContentFragment getActiveContentFragment(@NonNull FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentById(R.id.content);
        if (fragment instanceof BaseContentFragment) {
            return (BaseContentFragment) fragment;
        }
        return null;
    }
}