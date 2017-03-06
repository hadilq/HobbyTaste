package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;

/**
 * @author hadi
 * @since 1/15/2017 AD.
 */
public class AddStoreContentFragment extends BaseContentFragment implements OnMapReadyCallback {

    public static final String BUNDLE_KEY_DIALOG_RESULT_EVENT = "BUNDLE_KEY_DIALOG_RESULT_EVENT";
    private static final String BUNDLE_KEY_STORE = "BUNDLE_KEY_STORE";
    // Tehran
    private final double INITIAL_LAT = 35.7952119d;
    private final double INITIAL_LON = 51.4062329d;

    @BindView(R.id.input_layout_store_name)
    TextInputLayout mStoreNameInputLayout;
    @BindView(R.id.store_name)
    EditText mStoreNameEditText;
    @BindView(R.id.input_layout_store_description)
    TextInputLayout mStoreDescriptionInputLayout;
    @BindView(R.id.store_description)
    EditText mStoreDescriptionEditText;
    @BindView(R.id.controller)
    DialogControlLayout mController;

    private GoogleMap mMap;
    private LatLng mLatlng;

    public static AddStoreContentFragment instantiate(StoreSaveResultEvent event) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_DIALOG_RESULT_EVENT, event);
        AddStoreContentFragment fragment = new AddStoreContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.add_store_content_fragment, container, false);
        ButterKnife.bind(this, view);

        StoreModel store = getArguments().getParcelable(BUNDLE_KEY_STORE);
        if (store != null) {
            mLatlng = new LatLng(store.getLat(), store.getLon());
            setMarker(mLatlng);
        }

        mController.setCommitText(getString(R.string.next))
                   .arrange();
        mController.setOnControlListener(new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                actionGoToNext(
                        mLatlng,
                        mStoreNameEditText.getText().toString(),
                        mStoreDescriptionEditText.getText().toString());
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
                setMarker(latLng);
            }
        });
        LatLng latLng = new LatLng(INITIAL_LAT, INITIAL_LON);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        MapUtil.zoom(mMap, latLng);
    }

    private void setMarker(@NonNull LatLng latLng) {
        mMap.clear();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);
        mMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .icon(icon));
        mLatlng = latLng;
    }

    private void actionGoToNext(
            LatLng latLng,
            String storeName,
            String storeDescription
    ) {
        if (latLng == null) {
            mStoreNameInputLayout.setError(getString(R.string.new_store_marker_empty));
            return;
        }
        if (TextUtils.isEmpty(storeName)) {
            mStoreNameInputLayout.setError(getString(R.string.new_store_name_empty));
            return;
        }
        if (TextUtils.isEmpty(storeDescription)) {
            mStoreDescriptionInputLayout.setError(getString(R.string.new_store_description_empty));
            return;
        }
        mStoreNameInputLayout.setError("");
        mStoreDescriptionInputLayout.setError("");
        StoreModel store = new StoreModel(latLng.latitude, latLng.longitude, storeName, storeDescription);
        getArguments().putParcelable(BUNDLE_KEY_STORE, store);

        AddStoreContentFragment.StoreSaveResultEvent
                event = getArguments().getParcelable(BUNDLE_KEY_DIALOG_RESULT_EVENT);
        NavigationUtil.startContentFragment(getFragmentManager(), AddBannerContentFragment.instantiate(
                event, store));
    }

    public static class StoreSaveResultEvent extends BaseDialogFragment.BaseOnDialogResultEvent {
        private StoreModel mStoreModel;

        StoreSaveResultEvent(
                @NonNull String sourceTag
        ) {
            super(sourceTag);
        }

        void setStoreModel(StoreModel store) {
            this.mStoreModel = store;
        }

        StoreModel getStoreModel() {
            return mStoreModel;
        }

        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(
                Parcel dest,
                int flags
        ) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(this.mStoreModel, flags);
        }

        protected StoreSaveResultEvent(Parcel in) {
            super(in);
            this.mStoreModel = in.readParcelable(StoreModel.class.getClassLoader());
        }

        public static final Creator<StoreSaveResultEvent> CREATOR = new Creator<StoreSaveResultEvent>() {
            @Override public StoreSaveResultEvent createFromParcel(Parcel source) {
                return new StoreSaveResultEvent(source);
            }

            @Override public StoreSaveResultEvent[] newArray(int size) {
                return new StoreSaveResultEvent[size];
            }
        };
    }
}
