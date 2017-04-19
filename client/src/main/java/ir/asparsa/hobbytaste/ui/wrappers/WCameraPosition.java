package ir.asparsa.hobbytaste.ui.wrappers;

import android.support.annotation.NonNull;
import com.google.android.gms.maps.model.CameraPosition;

/**
 * @author hadi
 * @since 4/19/2017 AD.
 */
public class WCameraPosition {
    private final CameraPosition cameraPosition;

    public WCameraPosition(@NonNull CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public CameraPosition getRealCameraPosition() {
        return cameraPosition;
    }

    public WLatLng getTarget() {
        return new WLatLng(cameraPosition.target);
    }

    public float getZoom() {
        return cameraPosition.zoom;
    }
}
