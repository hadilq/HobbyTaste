package ir.asparsa.hobbytaste.core.route;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import ir.asparsa.hobbytaste.R;
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

    public static final int NO_POSITION = -1;

    private WeakReference<Collection<Route>> mRouts = new WeakReference<>(null);
    private List<WeakReference<ContainerFragment>> mContainers = new ArrayList<>();
    private List<Route> mRoutes = new ArrayList<>();
    private int currentPosition = NO_POSITION;

    @Inject
    LanguageUtil mLanguageUtil;

    @Inject
    public RouteFactory() {
        for (int i = 0; i < NavigationAdapter.PAGE_COUNT; i++) {
            // Make them fixed size by not adding anything else
            mContainers.add(new WeakReference<ContainerFragment>(null));
        }
    }

    @NonNull
    public Route getBellowRoute(
            Resources resources,
            int pos
    ) {
        int page = posToPage(pos);
        switch (page) {
            case MainRoute.PAGE:
                return new MainRoute(resources);
            case SettingsRoute.PAGE:
                return new SettingsRoute(resources);
        }
        Assert.fail("Shouldn't reach here");
        return new MainRoute(resources);
    }

    @Nullable
    private Route getRoute(
            @NonNull Resources resources,
            @NonNull Uri uri,
            @NonNull Bundle bundle
    ) {
        AnalysedUri analysedUri = new AnalysedUri(uri, bundle);
        for (Route route : generateRoutes(resources)) {
            if (route.shouldFire(analysedUri)) {
                route.fire(analysedUri);
                return route;
            }
        }
        return null;
    }

    public boolean handleIntent(
            @NonNull Resources resources,
            @NonNull Intent intent,
            @NonNull ViewPager viewPager
    ) {
        // Refill routes in case of changing language
        mRoutes.clear();
        for (int i = 0; i < NavigationAdapter.PAGE_COUNT; i++) {
            // Make them fixed size by not adding anything else
            mRoutes.add(getBellowRoute(resources, i));
        }

        Uri uri = intent.getData();
        Route route = null;
        if (uri != null) {
            route = getRoute(resources, uri, intent.getExtras());
        }
        if (route == null) {
            return false;
        }
        int pos = pageToPos(route.whichPage());
        mRoutes.set(pos, route);
        launchRoute(route, pos);
        viewPager.setCurrentItem(pos);
        return true;
    }

    public void launchRoute(
            @NonNull Resources resources,
            int pos
    ) {
        launchRoute(getBellowRoute(resources, pos), pos);
    }

    public void launchRoute(
            Route route,
            int pos
    ) {
        ContainerFragment container = mContainers.get(pos).get();
        if (container != null) {
            container.launchRoute(route);
        }
    }

    @NonNull
    public Route getContainerRoute(int pos) {
        return mRoutes.get(pos);
    }

    @Nullable
    public FragmentManager getFragmentManager(int pos) {
        this.currentPosition = pos;
        ContainerFragment containerFragment = mContainers.get(pos).get();
        if (containerFragment == null) {
            return null;
        }
        return containerFragment.getChildFragmentManager();
    }

    public int getCurrentPosition() {
        return currentPosition;
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
    private Collection<Route> generateRoutes(@NonNull Resources resources) {
        Collection<Route> routes;
        routes = mRouts.get();
        if (routes == null) {
            routes = new ArrayDeque<>();
            routes.add(new MainRoute(resources));
            routes.add(new SettingsRoute(resources));
            routes.add(new PlaceRoute(resources));
            routes.add(new PlacesRoute(resources));
            mRouts = new WeakReference<>(routes);
        }
        return routes;
    }

    @Nullable
    public Uri.Builder getInternalUriBuilder(
            @NonNull Resources resources,
            @NonNull Class<? extends Route> clazz
    ) {
        return completeUri(resources, clazz, new Uri.Builder()
                .scheme(resources.getString(R.string.scheme_free))
                .authority(resources.getString(R.string.host_map)));
    }

    @Nullable
    public Uri.Builder getShareUriBuilder(
            @NonNull Resources resources,
            @NonNull Class<? extends Route> clazz
    ) {
        return completeUri(resources, clazz, new Uri.Builder()
                .scheme(resources.getString(R.string.scheme_http))
                .authority(resources.getString(R.string.host_free_map)));
    }

    @Nullable
    private Uri.Builder completeUri(
            @NonNull Resources resources,
            @NonNull Class<? extends Route> clazz,
            @NonNull Uri.Builder uriBuilder
    ) {
        for (Route route : generateRoutes(resources)) {
            if (clazz.equals(route.getClass())) {
                return route.addPath(uriBuilder, resources);
            }
        }
        return null;
    }

    public int pageToPos(int page) {
        return mLanguageUtil.isRTL() ? NavigationAdapter.PAGE_COUNT - 1 - page : page;
    }

    public int posToPage(int pos) {
        // The names are important here. The result of these two methods are the same
        return pageToPos(pos);
    }
}
