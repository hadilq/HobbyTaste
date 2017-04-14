package ir.asparsa.hobbytaste.ui.mvp.holder;

import android.support.annotation.Nullable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import ir.asparsa.hobbytaste.ui.mvp.presenter.StorePresenter;

/**
 * @author hadi
 * @since 4/14/2017 AD.
 */
public class MainContentViewHolder implements ViewHolder, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private final StorePresenter mPresenter;
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
        mMap = googleMap;
        mPresenter.publish();
    }

    @Nullable
    public GoogleMap getMap() {
        return mMap;
    }
}
