package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;

/**
 * Created by hadi on 1/15/2017 AD.
 */
public class AddStoreContentFragment extends BaseContentFragment implements OnMapReadyCallback {

    @BindView(R.id.input_layout_store_name)
    TextInputLayout mStoreNameInputLayout;
    @BindView(R.id.store_name)
    EditText mStoreNameEditText;
    @BindView(R.id.controller)
    DialogControlLayout mController;

    private GoogleMap mMap;
    private Marker mMarker;

    public static AddStoreContentFragment instantiate() {
        AddStoreContentFragment fragment = new AddStoreContentFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.add_store_content_fragment, container, false);
        ButterKnife.bind(this, view);

        mController.setCommitText(getString(R.string.commit))
                   .arrange();
        mController.setOnControlListener(new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                actionGoToNext(mMarker, mStoreNameEditText.getText().toString());
            }

            @Override public void onNeutral() {
            }

            @Override public void onCancel() {
            }
        });

        return view;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fragment fragment = NavigationUtil.getActiveFragment(getChildFragmentManager());
        SupportMapFragment mapFragment;
        if (fragment instanceof SupportMapFragment) {
            mapFragment = (SupportMapFragment) fragment;
        } else {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                                     .replace(R.id.content_nested, mapFragment)
                                     .commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_add_store);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override public void onMapClick(LatLng latLng) {
                mMap.clear();
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);
                mMarker = mMap.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .icon(icon));
            }
        });
    }

    private void actionGoToNext(
            Marker marker,
            String storeName
    ) {
        if (marker == null) {
            mStoreNameInputLayout.setError(getString(R.string.new_store_marker_empty));
            return;
        }
        if (TextUtils.isEmpty(storeName)) {
            mStoreNameInputLayout.setError(getString(R.string.new_store_name_empty));
        }
        // TODO send the request
    }
}
