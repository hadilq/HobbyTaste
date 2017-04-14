package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;
import ir.asparsa.hobbytaste.ui.mvp.holder.MainContentViewHolder;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author hadi
 * @since 4/14/2017 AD.
 */
public class StorePresenter implements Presenter<MainContentViewHolder> {

    private static final String BUNDLE_KEY_CAMERA = "BUNDLE_KEY_CAMERA";
    private static final String BUNDLE_KEY_STORES = "BUNDLE_KEY_STORES";

    @Inject
    StoresManager mStoresManager;
    @Inject
    PreferencesManager mPreferencesManager;

    private final MainContentFragment mFragment;
    private MainContentViewHolder mHolder;
    private ArrayList<StoreModel> mStores;
    private List<Marker> mMarkers = new ArrayList<>();
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private boolean mTryAgainLater = true;

    public StorePresenter(
            MainContentFragment fragment
    ) {
        ApplicationLauncher.mainComponent().inject(this);
        this.mFragment = fragment;

        double lat = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LATITUDE, 0f);
        double lng = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LONGITUDE, 0f);
        float zoom = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_ZOOM, 0f);
        if (lat != 0f && lng != 0f && zoom != 0) {
            mFragment.getArguments()
                     .putParcelable(BUNDLE_KEY_CAMERA, CameraPosition.fromLatLngZoom(new LatLng(lat, lng), zoom));
        }

    }

    @Override public void bindView(@NonNull MainContentViewHolder viewHolder) {
        mHolder = viewHolder;
        mStores = mFragment.getArguments().getParcelableArrayList(BUNDLE_KEY_STORES);
        if (mStores != null) {
            mTryAgainLater = false;
        }

        if (mTryAgainLater) {
            mTryAgainLater = false;
            mSubscription.add(mStoresManager.loadStores(getStoreObserver()));
        } else {
            publish();
        }
    }

    @Override public void unbindView() {
        if (mHolder == null) {
            return;
        }

        GoogleMap map = mHolder.getMap();
        if (map != null) {
            CameraPosition cameraPosition = map.getCameraPosition();
            mFragment.getArguments().putParcelable(BUNDLE_KEY_CAMERA, cameraPosition);
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
        mHolder = null;
    }

    @Override public void publish() {
        if (mHolder == null) {
            return;
        }

        GoogleMap map = mHolder.getMap();
        if (map != null && mStores != null) {
            double accumulatedLat = 0d;
            double accumulatedLon = 0d;
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);

            removeMarkers(mMarkers);
            for (StoreModel store : mStores) {
                accumulatedLat += store.getLat();
                accumulatedLon += store.getLon();
                LatLng latLng = new LatLng(store.getLat(), store.getLon());

                Marker marker = map.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .title(store.getTitle())
                                .icon(icon));
                mMarkers.add(marker);
            }
            CameraPosition camera = mFragment.getArguments().getParcelable(BUNDLE_KEY_CAMERA);
            if (camera == null) {
                LatLng latLng = new LatLng(accumulatedLat / mStores.size(), accumulatedLon / mStores.size());
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            } else {
                map.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
            }
            map.setOnMarkerClickListener(mHolder);
        }
    }

    private void removeMarkers(Collection<Marker> mMarkers) {
        if (mMarkers.size() != 0) {
            for (Marker marker : mMarkers) {
                marker.remove();
            }
            mMarkers.clear();
        }
    }

    private Observer<Collection<StoreModel>> getStoreObserver() {
        return new Observer<Collection<StoreModel>>() {
            @Override public void onCompleted() {
                L.i(MainContentFragment.class, "Refresh request gets completed");
            }

            @Override public void onError(Throwable e) {
                L.w(MainContentFragment.class, "Store gets error", e);
                Toast.makeText(mFragment.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
        mFragment.getArguments().putParcelableArrayList(BUNDLE_KEY_STORES, mStores);
        publish();
    }


    public boolean onMarkerClick(Marker marker) {
        int index = mMarkers.indexOf(marker);
        if (index != -1) {
            mFragment.instantiateStoreDetail(mStores.get(index));
            return true;
        }
        return false;
    }

    public void onNewStore(StoreModel storeModel) {
        if (mStores != null) {
            mStores.add(storeModel);
        } else {
            mTryAgainLater = true;
        }
    }
}
