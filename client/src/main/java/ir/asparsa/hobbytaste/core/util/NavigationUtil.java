package ir.asparsa.hobbytaste.core.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.BaseFragment;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import junit.framework.Assert;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
@Singleton
public class NavigationUtil {

    @Inject
    public NavigationUtil() {
    }

    public void startContentFragment(
            @NonNull FragmentManager fragmentManager,
            @NonNull BaseContentFragment fragment
    ) {
        try {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.come, 0, 0, R.anim.back)
                    .replace(R.id.content, fragment, fragment.getTagName())
                    .addToBackStack(fragment.getTagName())
                    .commit();
        } catch (Exception e) {
            L.e(NavigationUtil.class.getClass(), "start content problem!", e);
        }
    }

    public void startNestedFragment(
            @NonNull FragmentManager fragmentManager,
            @NonNull BaseFragment fragment
    ) {
        try {
            fragmentManager.beginTransaction()
                           .setCustomAnimations(R.anim.back, 0)
                           .replace(R.id.content_nested, fragment, fragment.getTagName())
                           .commit();
        } catch (Exception e) {
            L.e(NavigationUtil.class.getClass(), "start nested problem!", e);
        }
    }

    public void popBackStack(@NonNull FragmentManager activeFragmentManager) {
        try {
            activeFragmentManager.popBackStack();
        } catch (Exception e) {
            L.e(NavigationUtil.class.getClass(), "Back problem!", e);
        }
    }

    @Nullable
    public BaseContentFragment findTopFragment(
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

    public Fragment getActiveFragment(@NonNull FragmentManager fragmentManager) {
        return fragmentManager.findFragmentById(R.id.content_nested);
    }

    public BaseContentFragment getActiveContentFragment(@NonNull FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentById(R.id.content);
        if (fragment instanceof BaseContentFragment) {
            return (BaseContentFragment) fragment;
        }
        return null;
    }
}