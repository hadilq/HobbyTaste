package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.core.manager.RefreshManager;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class MainContentFragment extends BaseContentFragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String FRAGMENT_TAG = "main";

    @Inject
    AuthorizationManager mAuthorizationManager;
    @Inject
    RefreshManager mRefreshManager;
    @Inject
    StoresManager mStoresManager;

    private GoogleMap mMap;
    private List<StoreModel> mStores;
    private List<Marker> mMarkers = new ArrayList<>();
    private boolean mIsCameraMovedBefore = false;
    private CompositeSubscription subscription = new CompositeSubscription();

    public static MainContentFragment instantiate() {
        MainContentFragment fragment = new MainContentFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
    }

    private Subscriber<Collection<StoreModel>> getDatabaseSubscriber() {
        return new Subscriber<Collection<StoreModel>>() {
            @Override public void onCompleted() {
                L.i(MainContentFragment.class, "Refresh request gets completed");
            }

            @Override public void onError(Throwable e) {
                L.w(MainContentFragment.class, "Database gets error", e);
            }

            @Override public void onNext(Collection<StoreModel> stores) {
                onSuccessfullyReceived(stores);
            }
        };
    }

    private void onSuccessfullyReceived(Collection<StoreModel> stores) {
        L.i(MainContentFragment.class, "Stores successfully received: " + stores);
        if (stores.size() == 0) {
            return;
        }
        mStores = new ArrayList<>(stores);
        if (mMarkers.size() != 0) {
            removeMarkers(mMarkers);
        }
        fillMap();
    }

    private void removeMarkers(Collection<Marker> mMarkers) {
        for (Marker marker : mMarkers) {
            marker.remove();
        }
    }


    private void fillMap() {
        if (mMap != null && mStores != null) {
            double accumulatedLat = 0d;
            double accumulatedLon = 0d;
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);

            mMarkers.clear();
            for (StoreModel store : mStores) {
                accumulatedLat += store.getLat();
                accumulatedLon += store.getLon();
                LatLng latLng = new LatLng(store.getLat(), store.getLon());

                Marker marker = mMap.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .title(store.getTitle())
                                .icon(icon));
                mMarkers.add(marker);
            }
            if (!mIsCameraMovedBefore) {
                LatLng latLng = new LatLng(accumulatedLat / mStores.size(), accumulatedLon / mStores.size());
                mIsCameraMovedBefore = true;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                MapUtil.zoom(mMap, latLng);
            }
            mMap.setOnMarkerClickListener(this);
        }
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        subscription.add(mStoresManager.getStoresObservable().subscribe(getDatabaseSubscriber()));
        subscription.add(mStoresManager.subscribe(getDatabaseSubscriber()));

        mRefreshManager.refreshStores();
        return inflater.inflate(R.layout.main_content_fragment, container, false);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fragment fragment = NavigationUtil.getActiveFragment(getChildFragmentManager());
        SupportMapFragment mapFragment;
        if (fragment instanceof SupportMapFragment) {
            mapFragment = (SupportMapFragment) fragment;
        } else {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                                     .replace(R.id.content_nested, mapFragment)
                                     .commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override public void onDestroyView() {
        mIsCameraMovedBefore = false;
        for (Marker marker : mMarkers) {
            marker.remove();
        }
        subscription.clear();
        super.onDestroyView();
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_main);
    }

    @Override public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    @Override public BackState onBackPressed() {
        return BackState.CLOSE_APP;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fillMap();
    }

    @Override public boolean onMarkerClick(Marker marker) {
        for (Marker mMarker : mMarkers) {
            if (marker.equals(mMarker)) {
                NavigationUtil.startContentFragment(
                        getFragmentManager(),
                        StoreDetailsContentFragment.instantiate(mStores.get(mMarkers.indexOf(mMarker)))
                );
                return true;
            }
        }
        return false;
    }
}
