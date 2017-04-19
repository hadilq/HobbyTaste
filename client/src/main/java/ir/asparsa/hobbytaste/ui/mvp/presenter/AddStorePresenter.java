package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.fragment.content.AddStoreContentFragment;
import ir.asparsa.hobbytaste.ui.mvp.holder.AddStoreContentViewHolder;
import ir.asparsa.hobbytaste.ui.mvp.holder.FragmentHolder;
import junit.framework.Assert;

/**
 * @author hadi
 * @since 4/19/2017 AD.
 */
public class AddStorePresenter implements Presenter<AddStoreContentViewHolder> {

    public static final String BUNDLE_KEY_STORE = "BUNDLE_KEY_STORE";
    public static final String BUNDLE_KEY_LAT_LNG = "BUNDLE_KEY_LAT_LNG";
    public static final String BUNDLE_KEY_CAMERA_POSITION = "BUNDLE_KEY_CAMERA_POSITION";

    private final FragmentHolder mFragment;
    private Marker mMarker;
    private AddStoreContentViewHolder mHolder;

    public AddStorePresenter(FragmentHolder fragment) {
        this.mFragment = fragment;
    }

    @Override public void bindView(@NonNull AddStoreContentViewHolder viewHolder) {
        mHolder = viewHolder;
        publish();
    }

    @Override public void unbindView() {
        if (mMarker != null) {
            mFragment.getArguments().putParcelable(BUNDLE_KEY_LAT_LNG, mMarker.getPosition());
            mMarker.remove();
        }
        if (mHolder != null && mHolder.getMap() != null) {
            mFragment.getArguments().putParcelable(
                    BUNDLE_KEY_CAMERA_POSITION,
                    mHolder.getMap().getCameraPosition().getRealCameraPosition());
            mHolder = null;
        }
    }

    @Override public void publish() {
        if (mHolder == null || mHolder.getMap() == null) {
            return;
        }
        mHolder.getMap().moveCamera(mFragment.getArguments().<CameraPosition>getParcelable(BUNDLE_KEY_CAMERA_POSITION));
        StoreModel store = mFragment.getArguments().getParcelable(BUNDLE_KEY_STORE);
        if (store != null) {
            setMarker(new LatLng(store.getLat(), store.getLon()));
        }
        LatLng latLng = mFragment.getArguments().getParcelable(BUNDLE_KEY_LAT_LNG);
        if (mMarker == null && latLng != null) {
            setMarker(latLng);
        }
    }

    public void actionGoToNext() {
        if (mHolder == null || mHolder.getMap() == null) {
            return;
        }
        String name = mHolder.getName();
        String description = mHolder.getDescription();
        if (mMarker == null) {
            mHolder.setNameError(mFragment.getContext().getString(R.string.new_store_marker_empty));
            return;
        }
        if (TextUtils.isEmpty(name)) {
            mHolder.setNameError(mFragment.getContext().getString(R.string.new_store_name_empty));
            return;
        }
        if (TextUtils.isEmpty(description)) {
            mHolder.setDescriptionError(mFragment.getContext().getString(R.string.new_store_description_empty));
            return;
        }
        mHolder.setNameError("");
        mHolder.setDescriptionError("");

        LatLng latLng = mMarker.getPosition();
        mFragment.getArguments().putParcelable(
                BUNDLE_KEY_STORE,
                new StoreModel(latLng.latitude, latLng.longitude, name, description));

        mFragment.onClick(
                AddStoreContentFragment.EVENT_KEY_START_NEXT,
                new StoreModel(latLng.latitude, latLng.longitude, name, description));
    }

    public void setMarker(@NonNull LatLng latLng) {
        Assert.assertNotNull(mHolder);
        mMarker = mHolder.setMarker(latLng);
    }
}
