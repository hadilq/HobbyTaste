package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.fragment.content.FragmentDelegate;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;
import ir.asparsa.hobbytaste.ui.mvp.holder.MainContentViewHolder;
import ir.asparsa.hobbytaste.ui.wrapper.WCameraPosition;
import ir.asparsa.hobbytaste.ui.wrapper.WMap;
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
    private WCameraPosition mCameraPosition;

    @Inject
    StoresManager mStoresManager;
    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    MapUtil mMapUtil;

    private final FragmentDelegate mFragment;
    private MainContentViewHolder mHolder;
    private ArrayList<StoreModel> mStores = new ArrayList<>();
    private List<Marker> mMarkers = new ArrayList<>();
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private boolean mTryAgainLater = true;
    private int mOffset;
    private boolean mOnMapReady = false;

    public StorePresenter(
            FragmentDelegate fragment
    ) {
        ApplicationLauncher.mainComponent().inject(this);
        this.mFragment = fragment;
    }

    @Override public void bindView(@NonNull MainContentViewHolder viewHolder) {
        L.d(getClass(), "bind view gets called. " + mStores);
        mHolder = viewHolder;
        ArrayList<StoreModel> stores = mFragment.getArguments().getParcelableArrayList(BUNDLE_KEY_STORES);
        if (stores != null) {
            addNewStores(stores);
        }

        double lat = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LATITUDE, 0f);
        double lng = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LONGITUDE, 0f);
        float zoom = mPreferencesManager.getFloat(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_ZOOM, 0f);
        if (lat != 0f && lng != 0f && zoom != 0) {
            mCameraPosition = new WCameraPosition(mMapUtil.fromLatLngZoom(lat, lng, zoom));
        }
        if (mTryAgainLater) {
            mTryAgainLater = false;

            mOffset = mFragment.getArguments().getInt(BUNDLE_KEY_OFFSET, 0);
            startLoading(lat, lng, mOffset);
        } else {
            publish();
        }
    }

    @Override public void unbindView() {
        L.d(getClass(), "unbind view gets called. " + mStores);
        if (mHolder == null) {
            return;
        }

        WMap map = mHolder.getMap();
        if (map != null) {
            mCameraPosition = map.getCameraPosition();
            if (mCameraPosition != null && mCameraPosition.getTarget() != null) {
                mPreferencesManager.put(
                        PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LATITUDE,
                        (float) mCameraPosition.getTarget().getLatitude());
                mPreferencesManager.put(
                        PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_LONGITUDE,
                        (float) mCameraPosition.getTarget().getLongitude());
                mPreferencesManager.put(PreferencesManager.KEY_DEFAULT_CAMERA_POSITION_ZOOM, mCameraPosition.getZoom());
            }
        }
        mHolder.removeMarkers(mMarkers);
        mFragment.getArguments().putInt(BUNDLE_KEY_OFFSET, mOffset);
        mFragment.getArguments().putParcelableArrayList(BUNDLE_KEY_STORES, mStores);
        mHolder = null;
    }

    @Override public void publish() {
        L.d(getClass(), "publish gets called. " + mStores);
        if (mHolder == null) {
            return;
        }

        WMap map = mHolder.getMap();
        if (mOnMapReady && mCameraPosition != null && map != null) {
            mOnMapReady = false;
            map.moveCamera(mCameraPosition.getRealCameraPosition());
        }
        if (map != null && mStores.size() != 0) {

            mHolder.removeMarkers(mMarkers);
            for (StoreModel store : mStores) {
                LatLng latLng = new LatLng(store.getLat(), store.getLon());

                Marker marker = mHolder.addMarker(latLng, store.getTitle());
                mMarkers.add(marker);
            }

            if (mCameraPosition == null && mMarkers.size() != 0) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : mMarkers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                mCameraPosition = mHolder.getMap().getCameraPosition();
            }
            map.setOnMarkerClickListener(mHolder);
        }
    }

    private void startLoading(
            double lat,
            double lng,
            int offset
    ) {
        StoresManager.Constraint constraint = new StoresManager.Constraint(lat, lng, offset, STORES_LIMIT);
        mSubscription.add(mStoresManager.loadStores(constraint, getStoreObserver(constraint)));
    }

    public void onMapReady() {
        L.d(getClass(), "on map ready gets called.");
        mOnMapReady = true;
        publish();
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
                    double lat = constraint.getLatitude();
                    double lng = constraint.getLongitude();
                    if (mHolder.getMap() != null) {
                        ArrayList<StoreModel> list = new ArrayList<>(result.getStores());
                        StoreModel lastModel = list.get(list.size() - 1);
                        if (!mHolder.getMap().getProjection().getVisibleRegion().latLngBounds
                                .contains(new LatLng(lastModel.getLat(), lastModel.getLon()))) {
                            mCameraPosition = mHolder.getMap().getCameraPosition();
                            if (mCameraPosition != null && mCameraPosition.getTarget() != null) {
                                mOffset = 0;
                                lat = mCameraPosition.getTarget().getLatitude();
                                lng = mCameraPosition.getTarget().getLongitude();
                            }
                        }
                    }
                    startLoading(lat, lng, mOffset);
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
        if (stores.size() == 0) {
            return;
        }
        Collection<StoreModel> s = new ArrayDeque<>(stores);
        Iterator<StoreModel> iterator = s.iterator();
        do {
            if (mStores.contains(iterator.next())) {
                iterator.remove();
            }
        } while (iterator.hasNext());

        mStores.addAll(s);
    }


    public boolean onMarkerClick(Marker marker) {
        int index = mMarkers.indexOf(marker);
        if (index != -1) {
            mFragment.onEvent(MainContentFragment.EVENT_KEY_INSTANTIATE_STORE_DETAIL, mStores.get(index));
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

    public void onRefreshStores() {
        if (mHolder.getMap() != null) {
            mCameraPosition = mHolder.getMap().getCameraPosition();
            if (mCameraPosition != null && mCameraPosition.getTarget() != null) {
                mStores.clear();
                mOffset = 0;
                double lat = mCameraPosition.getTarget().getLatitude();
                double lng = mCameraPosition.getTarget().getLongitude();
                startLoading(lat, lng, mOffset);
            }
        }
    }
}
