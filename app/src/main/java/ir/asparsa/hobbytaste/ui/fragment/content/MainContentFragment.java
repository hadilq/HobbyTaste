package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class MainContentFragment extends BaseContentFragment implements OnMapReadyCallback {

    private static final String FRAGMENT_TAG = "main";

    private GoogleMap mMap;

    public static MainContentFragment instantiate() {
        MainContentFragment fragment = new MainContentFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_content_fragment, container, false);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fragment fragment = NavigationUtil.getActiveFragment(getFragmentManager());
        if (!(fragment instanceof SupportMapFragment)) {
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_nested, mapFragment)
                    .commit();
        }
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_main);
    }

    @Override public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    @Override public BackState onBackPressed() {
        return BackState.CLOSE_APP;
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(35.6940119d,51.4062329d);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        MapUtil.zoom(mMap, sydney);
    }
}
