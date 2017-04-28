package ir.asparsa.hobbytaste.core.util;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 * @since 11/29/2016 AD
 */
@Singleton
public class MapUtil {

    @Inject
    public MapUtil() {
    }

    public void zoom(
            GoogleMap googleMap,
            LatLng currentLocation
    ) {
        zoom(googleMap, currentLocation, 12);
    }

    public void zoom(
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

    public CameraPosition fromLatLngZoom(
            double lat,
            double lng,
            float zoom
    ) {
        return CameraPosition.fromLatLngZoom(new LatLng(lat, lng), zoom);
    }
}
