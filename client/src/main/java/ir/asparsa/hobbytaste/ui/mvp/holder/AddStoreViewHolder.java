package ir.asparsa.hobbytaste.ui.mvp.holder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.mvp.presenter.AddStorePresenter;
import ir.asparsa.hobbytaste.ui.wrapper.WMap;
import junit.framework.Assert;

/**
 * @author hadi
 * @since 4/19/2017 AD.
 */
public class AddStoreViewHolder implements ViewHolder, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private final View mView;
    private final AddStorePresenter mPresenter;

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

    private WMap mMap;
    private BitmapDescriptor mIcon;

    public AddStoreViewHolder(
            View view,
            AddStorePresenter presenter
    ) {
        mView = view;
        mPresenter = presenter;
        ButterKnife.bind(this, view);

        mStoreDescriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                    TextView v,
                    int actionId,
                    KeyEvent event
            ) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mPresenter.actionGoToNext();
                }
                return false;
            }
        });

        mController.setCommitText(view.getContext().getString(R.string.next))
                   .arrange();
        mController.setOnControlListener(new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                mPresenter.actionGoToNext();
            }

            @Override public void onNeutral() {
            }

            @Override public void onCancel() {
            }
        });
    }

    public Marker setMarker(@NonNull LatLng latLng) {
        Assert.assertNotNull(mMap);
        mMap.getRealMap().clear();
        return mMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .icon(mIcon));
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mIcon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);
        mMap = new WMap(googleMap);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override public void onMapClick(LatLng latLng) {
                mPresenter.setMarker(latLng);
            }
        });
        mPresenter.publish();
    }

    @Override public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Nullable
    public WMap getMap() {
        return mMap;
    }

    public String getName() {
        return mStoreNameEditText.getText().toString();
    }

    public String getDescription() {
        return mStoreDescriptionEditText.getText().toString();
    }

    public void setNameError(String error) {
        mStoreNameInputLayout.setError(error);
    }

    public void setDescriptionError(String error) {
        mStoreDescriptionInputLayout.setError(error);
    }
}
