package ir.asparsa.hobbytaste.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.list.data.PlaceData;
import rx.functions.Action1;

import javax.inject.Inject;

/**
 * @author hadi
 */
public class PlaceViewHolder extends BaseViewHolder<PlaceData> implements OnMapReadyCallback {

    @Inject
    MapUtil mMapUtil;

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.title)
    TextView mTitleView;
    @BindView(R.id.description)
    TextView mDescriptionView;

    private GoogleMap mMap;
    private StoreModel mStore;
    private Marker mMarker;
    private boolean mIsCameraMovedBefore = false;

    public PlaceViewHolder(
            View itemView,
            Action1<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
        ButterKnife.bind(this, itemView);

        // TODO why savedInstanceState is not working here?
        mMapView.onCreate(null);
        mMapView.onResume();
        mMapView.getMapAsync(this);
    }

    @Override public void onBindView(final PlaceData data) {
        L.d(getClass(), "On bind gets called");
        mStore = data.getStoreModel();

        mTitleView.setText(mStore.getTitle());
        mDescriptionView.setText(mStore.getDescription());
        publish();
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        L.i(this.getClass(), "On map ready called");
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        publish();
    }

    private void publish() {
        if (mMap != null && mStore != null) {
            LatLng latLng = new LatLng(mStore.getLat(), mStore.getLon());
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);

            mMarker = mMap.addMarker(
                    new MarkerOptions()
                            .position(latLng)
                            .title(mStore.getTitle())
                            .icon(icon)
            );

            if (!mIsCameraMovedBefore) {
                mIsCameraMovedBefore = true;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMapUtil.zoom(mMap, latLng);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (mObserver != null) {
                        mObserver.call(new OnPlaceClickEvent(mStore));
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        // TODO uncomment it after updating google services
//        mMapView.onStart();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        // TODO uncomment it after updating google services
//        mMapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
    }

    public void onLowMemory() {
    }

    public static class OnPlaceClickEvent implements BaseRecyclerFragment.Event {
        private StoreModel mStoreModel;

        OnPlaceClickEvent(StoreModel storeModel) {
            this.mStoreModel = storeModel;
        }

        public StoreModel getStoreModel() {
            return mStoreModel;
        }
    }

}
