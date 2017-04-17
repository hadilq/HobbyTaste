package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;
import ir.asparsa.hobbytaste.ui.mvp.holder.MainContentViewHolder;
import junit.framework.Assert;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;
import java.util.*;

/**
 * @author hadi
 * @since 4/14/2017 AD.
 */
public class StorePresenter implements Presenter<MainContentViewHolder> {

    private static final String BUNDLE_KEY_STORES = "BUNDLE_KEY_STORES";
    private static final String BUNDLE_KEY_OFFSET = "BUNDLE_KEY_OFFSET";
    private static final int STORES_LIMIT = 20;
    private CameraPosition mCameraPosition;

    @Inject
    StoresManager mStoresManager;
    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    MapUtil mMapUtil;

    private final MainContentFragment mFragment;
    private MainContentViewHolder mHolder;
    private ArrayList<StoreModel> mStores = new ArrayList<>();
    private List<Marker> mMarkers = new ArrayList<>();
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private boolean mTryAgainLater = true;
    private int mOffset;

    public StorePresenter(
            MainContentFragment fragment
    ) {
        ApplicationLauncher.mainComponent().inject(this);
        this.mFragment = fragment;

        double lat = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LATITUDE, 0f);
        double lng = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LONGITUDE, 0f);
        float zoom = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_ZOOM, 0f);
        if (lat != 0f && lng != 0f && zoom != 0) {
            mCameraPosition = mMapUtil.fromLatLngZoom(lat, lng, zoom);
        }
    }

    @Override public void bindView(@NonNull MainContentViewHolder viewHolder) {
        L.d(getClass(), "bind view gets called. " + mStores);
        mHolder = viewHolder;
        ArrayList<StoreModel> stores = mFragment.getArguments().getParcelableArrayList(BUNDLE_KEY_STORES);
        if (stores != null) {
            addNewStores(stores);
        }

        if (mTryAgainLater) {
            mTryAgainLater = false;

            double latitude = 0d;
            double longitude = 0d;

            if (mCameraPosition != null && mCameraPosition.target != null) {
                latitude = mCameraPosition.target.latitude;
                longitude = mCameraPosition.target.longitude;
            }
            mOffset = mFragment.getArguments().getInt(BUNDLE_KEY_OFFSET, 0);
            StoresManager.Constraint constraint = new StoresManager.Constraint(
                    latitude, longitude, mOffset, STORES_LIMIT);
            mSubscription.add(mStoresManager.loadStores(constraint, getStoreObserver(constraint)));
        } else {
            publish();
        }
    }

    @Override public void unbindView() {
        L.d(getClass(), "unbind view gets called. " + mStores);
        if (mHolder == null) {
            return;
        }

        GoogleMap map = mHolder.getMap();
        if (map != null) {
            mCameraPosition = map.getCameraPosition();
            if (mCameraPosition != null && mCameraPosition.target != null) {
                mPreferencesManager.put(
                        PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LATITUDE,
                        (float) mCameraPosition.target.latitude);
                mPreferencesManager.put(
                        PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LONGITUDE,
                        (float) mCameraPosition.target.longitude);
                mPreferencesManager.put(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_ZOOM, mCameraPosition.zoom);
            }
        }
        for (Marker marker : mMarkers) {
            marker.remove();
        }
        mHolder = null;
    }

    @Override public void publish() {
        L.d(getClass(), "publish gets called. " + mStores);
        if (mHolder == null) {
            return;
        }

        GoogleMap map = mHolder.getMap();
        if (map != null && mStores != null) {
            double accumulatedLat = 0d;
            double accumulatedLon = 0d;

            mHolder.removeMarkers(mMarkers);
            for (StoreModel store : mStores) {
                accumulatedLat += store.getLat();
                accumulatedLon += store.getLon();
                LatLng latLng = new LatLng(store.getLat(), store.getLon());

                Marker marker = mHolder.addMarker(latLng, store.getTitle());
                mMarkers.add(marker);
            }

            if (mCameraPosition == null) {
                LatLng latLng = new LatLng(accumulatedLat / mStores.size(), accumulatedLon / mStores.size());
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mCameraPosition = mHolder.getMap().getCameraPosition();
            }
            map.setOnMarkerClickListener(mHolder);
        }
    }

    public void onMapReady() {
        L.d(getClass(), "on map ready gets called.");
        Assert.assertNotNull(mHolder);
        Assert.assertNotNull(mHolder.getMap());
        if (mCameraPosition != null) {
            mHolder.getMap().moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        }
    }

    private Observer<StoresManager.SoresResult> getStoreObserver(final StoresManager.Constraint constraint) {
        return new Observer<StoresManager.SoresResult>() {
            @Override public void onCompleted() {
                L.i(MainContentFragment.class, "Refresh request gets completed");
            }

            @Override public void onError(Throwable e) {
                L.w(MainContentFragment.class, "Store gets error", e);
                Toast.makeText(mFragment.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                mTryAgainLater = true;
            }

            @Override public void onNext(StoresManager.SoresResult result) {
                if (result.getStores() == null || result.getStores().size() == 0) {
                    return;
                }

                if (mOffset + STORES_LIMIT < result.getTotalElements()) {
                    mOffset += STORES_LIMIT;
                    mFragment.getArguments().putInt(BUNDLE_KEY_OFFSET, mOffset);
                    StoresManager.Constraint newConstraint = new StoresManager.Constraint(
                            constraint.getLatitude(), constraint.getLongitude(), mOffset, STORES_LIMIT);
                    mSubscription.add(mStoresManager.loadStores(newConstraint, getStoreObserver(newConstraint)));
                }
                onSuccessfullyReceived(result.getStores());
            }
        };
    }

    private void onSuccessfullyReceived(Collection<StoreModel> stores) {
        L.i(MainContentFragment.class, "Stores successfully received: " + stores);

        addNewStores(stores);
        publish();
    }

    private void addNewStores(@NonNull Collection<StoreModel> stores) {
        Collection<StoreModel> s = new ArrayDeque<>(stores);
        Iterator<StoreModel> iterator = s.iterator();
        do {
            if (mStores.contains(iterator.next())) {
                iterator.remove();
            }
        } while (iterator.hasNext());

        mStores.addAll(s);
        mFragment.getArguments().putParcelableArrayList(BUNDLE_KEY_STORES, mStores);
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
