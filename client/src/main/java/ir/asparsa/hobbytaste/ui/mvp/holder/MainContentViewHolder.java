package ir.asparsa.hobbytaste.ui.mvp.holder;

import android.support.annotation.Nullable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.mvp.presenter.StorePresenter;

import java.util.Collection;

/**
 * @author hadi
 * @since 4/14/2017 AD.
 */
public class MainContentViewHolder implements ViewHolder, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private final StorePresenter mPresenter;
    private BitmapDescriptor mIcon;
    private GoogleMap mMap;

    public MainContentViewHolder(StorePresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override public boolean onMarkerClick(Marker marker) {
        return mPresenter.onMarkerClick(marker);
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
    @Override public void onMapReady(GoogleMap googleMap) {
        mIcon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);
        mMap = googleMap;
        mPresenter.onMapReady();
    }

    @Nullable
    public GoogleMap getMap() {
        return mMap;
    }

    public void removeMarkers(Collection<Marker> mMarkers) {
        if (mMarkers.size() != 0) {
            for (Marker marker : mMarkers) {
                marker.remove();
            }
            mMarkers.clear();
        }
    }

    public Marker addMarker(
            LatLng latLng,
            String title
    ) {
        return mMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .icon(mIcon));
    }
}