package ir.asparsa.hobbytaste.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.core.util.UiUtil;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.ui.list.data.StoreMapData;

import javax.inject.Inject;

/**
 * @author hadi
 */
public class StoreMapViewHolder extends BaseViewHolder<StoreMapData> implements OnMapReadyCallback {

    @Inject
    MapUtil mMapUtil;
    @Inject
    UiUtil mUiUtil;

    @BindView(R.id.map)
    MapView mMapView;

    private GoogleMap mMap;
    private boolean mIsCameraMovedBefore = false;

    public StoreMapViewHolder(
            View itemView
    ) {
        super(itemView);
        ApplicationLauncher.mainComponent().inject(this);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindView(StoreMapData data) {
        super.onBindView(data);
        fillMap();
        L.i(this.getClass(), "Bound");
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        // TODO why savedInstanceState is not working here?
        mMapView.onCreate(null);
        mMapView.onResume();
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        L.i(this.getClass(), "On map ready called");
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        fillMap();
    }

    private void fillMap() {
        if (mMap != null && getData() != null) {
            LatLng latLng = new LatLng(getData().getStore().getLat(), getData().getStore().getLon());
            BitmapDescriptor icon = BitmapDescriptorFactory
                    .fromBitmap(mUiUtil.getBitmapFromVectorDrawable(R.drawable.ic_placeholder));

            mMap.addMarker(
                    new MarkerOptions()
                            .position(latLng)
                            .title(getData().getStore().getTitle())
                            .icon(icon)
            );

            if (!mIsCameraMovedBefore) {
                mIsCameraMovedBefore = true;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMapUtil.zoom(mMap, latLng);
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
