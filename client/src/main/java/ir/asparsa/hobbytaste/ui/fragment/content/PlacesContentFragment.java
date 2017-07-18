package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.ui.fragment.recycler.PlacesRecyclerFragment;
import rx.functions.Action1;

import javax.inject.Inject;

/**
 * @author hadi
 */
public class PlacesContentFragment extends BaseContentFragment {

    @Inject
    NavigationUtil mNavigationUtil;
    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    MapUtil mMapUtil;

    public static PlacesContentFragment instantiate(
            double lat,
            double lng
    ) {

        Bundle bundle = new Bundle();
        bundle.putDouble(PlacesRecyclerFragment.BUNDLE_KEY_LAT, lat);
        bundle.putDouble(PlacesRecyclerFragment.BUNDLE_KEY_LNG, lng);

        PlacesContentFragment fragment = new PlacesContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Fragment fragment = mNavigationUtil.getActiveFragment(getChildFragmentManager());
        PlacesRecyclerFragment placesRecyclerFragment;

        if (!(fragment instanceof PlacesRecyclerFragment)) {
            placesRecyclerFragment = PlacesRecyclerFragment.instantiate(new Bundle(getArguments()));

            mNavigationUtil.startNestedFragment(
                    getChildFragmentManager(),
                    placesRecyclerFragment
            );
        } else {
            placesRecyclerFragment = (PlacesRecyclerFragment) fragment;
        }

        placesRecyclerFragment.setContentObserver(geRecyclerObserver());
    }

    private <T> Action1<T> geRecyclerObserver() {
        return new Action1<T>() {
            @Override public void call(T t) {
            }
        };
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_places);
    }

    @Override public BackState onBackPressed() {
        return BackState.BACK_FRAGMENT;
    }

    @Override public FloatingActionButtonObserver getFloatingActionButtonObserver() {
        return new FloatingActionButtonObserver() {
            @Override public void onNext(View view) {
                double lat = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LATITUDE, 0f);
                double lng = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LONGITUDE, 0f);
                float zoom = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_ZOOM, 0f);
                mNavigationUtil.startContentFragment(
                        getFragmentManager(), AddStoreContentFragment.instantiate(
                                mMapUtil.fromLatLngZoom(lat, lng, zoom),
                                new AddStoreContentFragment.StoreSaveResultEvent(getTagName())));
            }
        };
    }

    @Override public void onEvent(BaseEvent event) {
        if (event instanceof AddStoreContentFragment.StoreSaveResultEvent) {
            AddStoreContentFragment.StoreSaveResultEvent result = (AddStoreContentFragment.StoreSaveResultEvent) event;
            L.i(PlacesContentFragment.class, "Added store: " + result.getStoreModel());
            Fragment fragment = mNavigationUtil.getActiveFragment(getChildFragmentManager());
            if (fragment instanceof PlacesRecyclerFragment) {
                ((PlacesRecyclerFragment) fragment).onInsertNewPlace(result.getStoreModel());
            }
        }
    }

    @Override public boolean hasHomeAsUp() {
        return true;
    }

    @Override public boolean scrollToolbar() {
        return true;
    }
}
