package ir.asparsa.hobbytaste.core.route;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.ui.adapter.NavigationAdapter;
import ir.asparsa.hobbytaste.ui.fragment.ContainerFragment;
import junit.framework.Assert;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author hadi
 * @since 4/25/2017 AD.
 */
@Singleton
public class RouteFactory {

    private WeakReference<Collection<Route>> mRouts = new WeakReference<>(null);
    private List<WeakReference<ContainerFragment>> mContainers = new ArrayList<>();
    private List<Route> mRoutes = new ArrayList<>();

    @Inject
    public RouteFactory() {
        for (int i = 0; i < NavigationAdapter.PAGE_COUNT; i++) {
            // Make them fixed size by not adding anything else
            mContainers.add(new WeakReference<ContainerFragment>(null));
        }
    }

    @NonNull
    public Route getBellowRoute(int pos) {
        int page = posToPage(pos);
        switch (page) {
            case MainRoute.PAGE:
                return new MainRoute();
            case SettingsRoute.PAGE:
                return new SettingsRoute();
        }
        Assert.fail("Shouldn't reach here");
        return new MainRoute();
    }

    @Nullable
    private Route getRoute(
            @NonNull Uri uri,
            @NonNull Bundle bundle
    ) {
        AnalysedUri analysedUri = new AnalysedUri(uri, bundle);
        for (Route route : generateRoutes()) {
            if (route.shouldFire(analysedUri)) {
                route.fire(analysedUri);
                return route;
            }
        }
        return null;
    }

    public boolean handleIntent(
            @NonNull Intent intent,
            @NonNull ViewPager viewPager
    ) {
        // Refill routes in case of changing language
        mRoutes.clear();
        for (int i = 0; i < NavigationAdapter.PAGE_COUNT; i++) {
            // Make them fixed size by not adding anything else
            mRoutes.add(getBellowRoute(i));
        }

        Uri uri = intent.getData();
        Route route = null;
        if (uri != null) {
            route = getRoute(uri, intent.getExtras());
        }
        if (route == null) {
            return false;
        }
        int pos = pageToPos(route.whichPage());
        ContainerFragment container = mContainers.get(pos).get();
        if (container == null) {
            mRoutes.set(pos, route);
        } else {
            container.launchRoute(pos, route);
        }
        viewPager.setCurrentItem(pos);
        return true;
    }

    @NonNull
    public Route getContainerRoute(int pos) {
        return mRoutes.get(pos);
    }

    @Nullable
    public FragmentManager getFragmentManager(int pos) {
        ContainerFragment containerFragment = mContainers.get(pos).get();
        if (containerFragment == null) {
            return null;
        }
        return containerFragment.getChildFragmentManager();
    }

    public void addContainer(
            FragmentManager.OnBackStackChangedListener listener,
            @NonNull ContainerFragment fragment,
            int pos
    ) {
        mContainers.set(pos, new WeakReference<>(fragment));
        fragment.getChildFragmentManager().addOnBackStackChangedListener(listener);
    }

    @NonNull
    private Collection<Route> generateRoutes() {
        Collection<Route> routes;
        routes = mRouts.get();
        if (routes == null) {
            routes = new ArrayDeque<>();
            routes.add(new MainRoute());
            routes.add(new SettingsRoute());
            mRouts = new WeakReference<>(routes);
        }
        return routes;
    }

    public int pageToPos(int page) {
        return LanguageUtil.isRTL() ? NavigationAdapter.PAGE_COUNT - 1 - page : page;
    }

    public int posToPage(int pos) {
        // The names are important here. The result of these two methods are the same
        return pageToPos(pos);
    }
}
