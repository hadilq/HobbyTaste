package ir.asparsa.hobbytaste.core.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.BaseFragment;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import junit.framework.Assert;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 */
@Singleton
public class NavigationUtil {

    @Inject
    LanguageUtil mLanguageUtil;
    @Inject
    Context mContext;
    @Inject
    Resources mResources;
    @Inject
    Handler mHandler;

    @Inject
    public NavigationUtil() {
    }

    public void startContentFragment(
            @NonNull final FragmentManager fragmentManager,
            @NonNull final BaseContentFragment fragment
    ) {
        try {
            FragmentTransaction t = fragmentManager.beginTransaction();
            if (mLanguageUtil.isRTL()) {
                t.setCustomAnimations(R.anim.enter_from_left, 0);
            } else {
                t.setCustomAnimations(R.anim.enter_from_right, 0);
            }
            t.add(R.id.content, fragment, fragment.getTagName())
             .addToBackStack(fragment.getTagName())
             .commit();
        } catch (Exception e) {
            L.e(NavigationUtil.class.getClass(), "start content problem!", e);
        }
    }

    public void popBackStack(
            @NonNull final FragmentManager fragmentManager,
            @NonNull final BaseContentFragment fragment
    ) {
        Animation animation;
        if (mLanguageUtil.isRTL()) {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.exit_to_left);
        } else {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.exit_to_right);
        }
        animation.setStartOffset(0);
        View view = fragment.getView();
        if (view != null) {
            view.startAnimation(animation);
        }
        mHandler.postDelayed(new Runnable() {
            @Override public void run() {
                try {
                    fragmentManager.popBackStack();
                } catch (Exception e) {
                    L.e(NavigationUtil.class.getClass(), "Back problem!", e);
                }
            }
        }, mResources.getInteger(R.integer.fragment_transition_duration));
    }

    public void startNestedFragment(
            @NonNull FragmentManager fragmentManager,
            @NonNull BaseFragment fragment
    ) {
        try {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_nested, fragment, fragment.getTagName())
                    .commit();
        } catch (Exception e) {
            L.e(NavigationUtil.class.getClass(), "start nested problem!", e);
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