package ir.asparsa.android.core.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.BaseFragment;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * @author hadi
 */
@Singleton
public class UiUtil {

    @Inject
    Context mContext;

    @Inject
    public UiUtil() {
    }

    public static void invokeEventReceiver(
            @NonNull BaseFragment.BaseEvent event,
            @NonNull FragmentManager fragmentManager
    ) {
        invokeEventReceiver(event, fragmentManager, false);
    }

    public static void invokeEventReceiver(
            @NonNull BaseFragment.BaseEvent event,
            @NonNull FragmentManager fragmentManager,
            boolean popBackStack
    ) {
        boolean reachTheTarget = false;
        for (int i = fragmentManager.getBackStackEntryCount() - 1; i >= 0; i--) {
            FragmentManager.BackStackEntry backStack = fragmentManager.getBackStackEntryAt(i);
            Fragment fragment = fragmentManager.findFragmentByTag(backStack.getName());
            if (fragment instanceof BaseFragment) {
                L.i(UiUtil.class, "Find base fragment to send event: " + fragment.getClass().getName());
                ((BaseFragment) fragment).onEvent(event);
                if (event.getSourceTag().equals(((BaseFragment) fragment).getTagName())) {
                    reachTheTarget = true;
                } else if (popBackStack && !reachTheTarget) {
                    try {
                        fragmentManager.popBackStack(fragment.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    } catch (Exception e) {
                        L.e(UiUtil.class.getClass(), "Back problem!", e);
                    }
                }
            }
        }
    }

    public static void invokeDialogEventReceiver(
            @NonNull BaseFragment.BaseEvent event,
            @NonNull FragmentManager fragmentManager
    ) {
        invokeDialogEventReceiver(event, fragmentManager, false);
    }

    public static void invokeDialogEventReceiver(
            @NonNull BaseFragment.BaseEvent event,
            @NonNull FragmentManager fragmentManager,
            boolean popBackStack
    ) {
        List<Fragment> fragments = fragmentManager.getFragments();
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment instanceof BaseFragment &&
                event.getSourceTag().equals(((BaseFragment) fragment).getTagName())) {
                L.i(UiUtil.class, "Find base fragment to send event: " + fragment.getClass().getName());
                ((BaseFragment) fragment).onEvent(event);
                break;
            } else if (popBackStack && fragment != null) {
                try {
                    fragmentManager.popBackStack(fragment.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (Exception e) {
                    L.e(UiUtil.class.getClass(), "Back problem!", e);
                }
            }
        }
    }

    public Bitmap getBitmapFromVectorDrawable(
            int drawableId
    ) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(mContext, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
