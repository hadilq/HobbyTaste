package ir.asparsa.hobbytaste.ui.mvp.holder;

import android.support.annotation.Nullable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;
import ir.asparsa.android.core.util.UiUtil;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.mvp.presenter.StorePresenter;
import ir.asparsa.hobbytaste.ui.wrapper.WMap;

import javax.inject.Inject;
import java.util.Collection;

/**
 * @author hadi
 * @since 4/14/2017 AD.
 */
public class MainContentViewHolder implements ViewHolder, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Inject
    UiUtil mUiUtil;

    private final StorePresenter mPresenter;
    private BitmapDescriptor mIcon;
    private WMap mMap;

    public MainContentViewHolder(StorePresenter presenter) {
        this.mPresenter = presenter;
        ApplicationLauncher.mainComponent().inject(this);
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
        mIcon = BitmapDescriptorFactory.fromBitmap(mUiUtil.getBitmapFromVectorDrawable(R.drawable.ic_placeholder));
        mMap = new WMap(googleMap);
        mPresenter.onMapReady();
    }

    @Nullable
    public WMap getMap() {
        return mMap;
    }

    public void removeMarkers(Collection<Marker> markers) {
        if (markers.size() != 0) {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
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
