package ir.asparsa.hobbytaste.ui.wrappers;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author hadi
 * @since 4/19/2017 AD.
 */
public class WMap {
    private final GoogleMap map;

    public WMap(GoogleMap map) {
        this.map = map;
    }

    public WCameraPosition getCameraPosition() {
        return new WCameraPosition(map.getCameraPosition());
    }

    public GoogleMap getRealMap() {
        return map;
    }

    public Marker addMarker(MarkerOptions markerOptions) {
        return map.addMarker(markerOptions);
    }

    public void animateCamera(CameraUpdate cameraUpdate) {
        map.animateCamera(cameraUpdate);
    }

    public void setOnMarkerClickListener(GoogleMap.OnMarkerClickListener listener) {
        map.setOnMarkerClickListener(listener);
    }

    public void moveCamera(CameraPosition cameraPosition) {
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public Projection getProjection() {
        return map.getProjection();
    }
}
