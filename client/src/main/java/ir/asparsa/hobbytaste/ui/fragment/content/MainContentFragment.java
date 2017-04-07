package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import rx.Observer;
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

    private static final String BUNDLE_KEY_CAMERA = "BUNDLE_KEY_CAMERA";

    @Inject
    AuthorizationManager mAuthorizationManager;
    @Inject
    StoresManager mStoresManager;
    @Inject
    PreferencesManager mPreferencesManager;

    private GoogleMap mMap;
    private List<StoreModel> mStores;
    private List<Marker> mMarkers = new ArrayList<>();
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private boolean mTryAgainLater = true;

    public static MainContentFragment instantiate() {
        MainContentFragment fragment = new MainContentFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);

        double lat = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LATITUDE, 0f);
        double lng = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LONGITUDE, 0f);
        float zoom = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_ZOOM, 0f);
        if (lat != 0f && lng != 0f && zoom != 0) {
            getArguments().putParcelable(BUNDLE_KEY_CAMERA, CameraPosition.fromLatLngZoom(new LatLng(lat, lng), zoom));
        }
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fragment fragment = NavigationUtil.getActiveFragment(getChildFragmentManager());
        SupportMapFragment mapFragment;
        if (!(fragment instanceof SupportMapFragment)) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                                     .replace(R.id.content_nested, mapFragment)
                                     .commit();
            mapFragment.getMapAsync(this);
        }
    }

    @Override public void onStart() {
        super.onStart();
        if (mTryAgainLater) {
            mTryAgainLater = false;
            mSubscription.add(mStoresManager.loadStores(getStoreObserver()));
        } else {
            fillMap();
        }
    }

    @Override public void onDestroyView() {
        if (mMap != null) {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            getArguments().putParcelable(BUNDLE_KEY_CAMERA, cameraPosition);
            if (cameraPosition != null && cameraPosition.target != null) {
                mPreferencesManager.put(
                        PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LATITUDE,
                        (float) cameraPosition.target.latitude);
                mPreferencesManager.put(
                        PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LONGITUDE,
                        (float) cameraPosition.target.longitude);
                mPreferencesManager.put(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_ZOOM, cameraPosition.zoom);
            }
        }
        for (Marker marker : mMarkers) {
            marker.remove();
        }
        mSubscription.clear();
        super.onDestroyView();
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.app_name);
    }

    private Observer<Collection<StoreModel>> getStoreObserver() {
        return new Observer<Collection<StoreModel>>() {
            @Override public void onCompleted() {
                L.i(MainContentFragment.class, "Refresh request gets completed");
            }

            @Override public void onError(Throwable e) {
                L.w(MainContentFragment.class, "Store gets error", e);
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                mTryAgainLater = true;
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
        fillMap();
    }

    private void removeMarkers(Collection<Marker> mMarkers) {
        if (mMarkers.size() != 0) {
            for (Marker marker : mMarkers) {
                marker.remove();
            }
            mMarkers.clear();
        }
    }

    private void fillMap() {
        if (mMap != null && mStores != null) {
            double accumulatedLat = 0d;
            double accumulatedLon = 0d;
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);

            removeMarkers(mMarkers);
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
            CameraPosition camera = getArguments().getParcelable(BUNDLE_KEY_CAMERA);
            if (camera == null) {
                LatLng latLng = new LatLng(accumulatedLat / mStores.size(), accumulatedLon / mStores.size());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
            }
            mMap.setOnMarkerClickListener(this);
        }
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
        int index = mMarkers.indexOf(marker);
        if (index != -1) {

            NavigationUtil.startContentFragment(
                    getFragmentManager(),
                    StoreDetailsContentFragment.instantiate(mStores.get(index))
            );
            return true;
        }
        return false;
    }

    @Override public FloatingActionButtonObserver getFloatingActionButtonObserver() {
        return new FloatingActionButtonObserver() {
            @Override public void onNext(View view) {
                NavigationUtil.startContentFragment(
                        getFragmentManager(), AddStoreContentFragment.instantiate(
                                mMap.getCameraPosition(),
                                new AddStoreContentFragment.StoreSaveResultEvent(getTagName())));
            }
        };
    }

    @Override public void onEvent(BaseEvent event) {
        if (event instanceof AddStoreContentFragment.StoreSaveResultEvent) {
            AddStoreContentFragment.StoreSaveResultEvent result = (AddStoreContentFragment.StoreSaveResultEvent) event;
            if (mStores != null) {
                mStores.add(result.getStoreModel());
            }
        }
    }
}
