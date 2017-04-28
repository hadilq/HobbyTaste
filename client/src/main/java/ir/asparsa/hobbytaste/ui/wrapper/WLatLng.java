package ir.asparsa.hobbytaste.ui.wrapper;

import android.support.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author hadi
 * @since 4/19/2017 AD.
 */
public class WLatLng {
    private final LatLng latLng;

    public WLatLng(@NonNull LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getRealLatLng() {
        return latLng;
    }

    public double getLatitude() {
        return latLng.latitude;
    }

    public double getLongitude() {
        return latLng.longitude;
    }
}
