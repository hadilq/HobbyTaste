package ir.asparsa.hobbytaste.core.util;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author hadi
 * @since 11/29/2016 AD
 */
public class MapUtil {
    public static void zoom(
            GoogleMap googleMap,
            LatLng currentLocation
    ) {
        zoom(googleMap, currentLocation, 12);
    }

    public static void zoom(
            GoogleMap googleMap,
            LatLng currentLocation,
            int zoomLevel
    ) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel), 2000, null);
    }
}
