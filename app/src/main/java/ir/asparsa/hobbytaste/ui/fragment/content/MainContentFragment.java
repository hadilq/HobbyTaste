package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.logger.L;
import ir.asparsa.hobbytaste.core.manager.RefreshManager;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import rx.Observer;

import javax.inject.Inject;
import java.util.ArrayDeque;
import java.util.Collection;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class MainContentFragment extends BaseContentFragment implements OnMapReadyCallback {

    private static final String FRAGMENT_TAG = "main";

    @Inject
    RefreshManager mRefreshManager;
    @Inject
    StoresManager mStoresManager;

    private GoogleMap mMap;
    private Collection<StoreModel> mStores;
    private Collection<Marker> mMarkers = new ArrayDeque<>();
    private Observer<Collection<StoreModel>> mObserver;
    private boolean mIsCameraMovedBefore = false;

    public static MainContentFragment instantiate() {
        MainContentFragment fragment = new MainContentFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);

        mStoresManager.getStores().subscribe(getObserver());

        mRefreshManager.addStoresObserver(getObserver());
        mRefreshManager.refreshStores();
    }

    private Observer<Collection<StoreModel>> getObserver() {
        if (mObserver == null) {
            mObserver = new Observer<Collection<StoreModel>>() {
                @Override public void onCompleted() {
                }

                @Override public void onError(Throwable e) {
                    L.i(MainContentFragment.class, "Error on get stores");
                    Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                }

                @Override public void onNext(Collection<StoreModel> stores) {
                    L.i(MainContentFragment.class, "Stores successfully received");
                    if (mMarkers.size() != 0) {
                        removeMarkers(mMarkers);
                    }
                    mStores = stores;
                    fillMap();
                }
            };
        }
        return mObserver;
    }

    private void removeMarkers(Collection<Marker> mMarkers) {
        for (Marker marker : mMarkers) {
            marker.remove();
        }
    }


    private void fillMap() {
        if (mMap != null && mStores != null) {
            for (StoreModel store : mStores) {
                LatLng sydney = new LatLng(store.getLat(), store.getLon());
                mMarkers.add(mMap.addMarker(new MarkerOptions().position(sydney).title(store.getTitle())));
            }
            LatLng latLng = new LatLng(35.6940119d, 51.4062329d);
            if (!mIsCameraMovedBefore) {
                mIsCameraMovedBefore = true;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                MapUtil.zoom(mMap, latLng);
            }
        }
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_content_fragment, container, false);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fragment fragment = NavigationUtil.getActiveFragment(getFragmentManager());
        if (!(fragment instanceof SupportMapFragment)) {
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_nested, mapFragment)
                    .commit();
        }
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
}
