package ir.asparsa.hobbytaste.ui.list.holder;

import android.os.Bundle;
import android.view.View;
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
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.list.data.StoreMapData;
import rx.functions.Action1;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreMapViewHolder extends BaseViewHolder<StoreMapData> implements OnMapReadyCallback {

    @BindView(R.id.map)
    MapView mMapView;

    private GoogleMap mMap;
    private StoreModel mStore;
    private Marker mMarker;
    private boolean mIsCameraMovedBefore = false;

    public StoreMapViewHolder(
            View itemView,
            Action1<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);

        // TODO why savedInstanceState is not working here?
        mMapView.onCreate(null);
        mMapView.onResume();
        mMapView.getMapAsync(this);
    }

    @Override
    public void onBindView(StoreMapData data) {
        mStore = data.getStore();
        fillMap();
        L.i(this.getClass(), "Bound");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        L.i(this.getClass(), "On map ready called");
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        fillMap();
    }

    private void fillMap() {
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
                MapUtil.zoom(mMap, latLng);
            }
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


}
