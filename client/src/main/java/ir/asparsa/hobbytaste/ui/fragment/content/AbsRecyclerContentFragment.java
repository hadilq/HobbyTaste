package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.hobbytaste.core.route.RouteFactory;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import rx.functions.Action1;

import javax.inject.Inject;

/**
 * @author hadi
 */
public abstract class AbsRecyclerContentFragment<P extends AbsListProvider, RF extends BaseRecyclerFragment<P>>
        extends BaseContentFragment {

    @Inject
    RouteFactory mRouteFactory;
    @Inject
    NavigationUtil mNavigationUtil;

    @SuppressWarnings("unchecked")
    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Fragment fragment = mNavigationUtil.getActiveFragment(getChildFragmentManager());
        Class<RF> clazz = onRecyclerFragmentClass();
        RF recyclerFragment = null;
        if (clazz.isInstance(fragment)) {
            recyclerFragment = (RF) fragment;
        } else {
            try {
                recyclerFragment = clazz.newInstance();
                recyclerFragment.setArguments(new Bundle(getArguments()));

                mNavigationUtil.startNestedFragment(
                        getChildFragmentManager(),
                        recyclerFragment
                );

            } catch (IllegalAccessException e) {
                L.w(getClass(), "Cannot launch recycler fragment", e);
            } catch (java.lang.InstantiationException e) {
                L.w(getClass(), "Cannot launch recycler fragment", e);
            }
        }

        if (recyclerFragment != null) {
            recyclerFragment.setContentObserver(geRecyclerObserver());
        }
    }

    abstract Class<RF> onRecyclerFragmentClass();

    <T extends BaseViewHolder> Action1<T> geRecyclerObserver() {
        return new Action1<T>() {
            @Override public void call(T t) {
            }
        };
    }
}
