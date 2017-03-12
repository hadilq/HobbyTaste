package ir.asparsa.android.core.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.BaseFragment;

import java.util.List;

/**
 * @author hadi
 * @since 3/12/2017 AD.
 */
public class UiUtil {

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
        List<Fragment> fragments = fragmentManager.getFragments();
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment instanceof BaseFragment &&
                event.getSourceTag().equals(((BaseFragment) fragment).getTagName())) {
                L.i(UiUtil.class, "Find base fragment to send event: " + fragment.getClass().getName());
                ((BaseFragment) fragment).onEvent(event);
                break;
            } else if (popBackStack) {
                try {
                    fragmentManager.popBackStack(fragment.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (Exception e) {
                    L.e(UiUtil.class.getClass(), "Back problem!", e);
                }
            }
        }
    }

}
