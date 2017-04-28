package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.mvp.holder.AddStoreViewHolder;
import ir.asparsa.hobbytaste.ui.mvp.presenter.AddStorePresenter;

/**
 * @author hadi
 * @since 1/15/2017 AD.
 */
public class AddStoreContentFragment extends BaseContentFragment {

    public static final String BUNDLE_KEY_DIALOG_RESULT_EVENT = "BUNDLE_KEY_DIALOG_RESULT_EVENT";
    public static final String EVENT_KEY_START_NEXT = "EVENT_KEY_START_NEXT";

    private AddStoreViewHolder mHolder;
    private AddStorePresenter mPresenter;

    public static AddStoreContentFragment instantiate(
            @NonNull CameraPosition cameraPosition,
            @NonNull StoreSaveResultEvent event
    ) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AddStorePresenter.BUNDLE_KEY_CAMERA_POSITION, cameraPosition);
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
        mPresenter = new AddStorePresenter(getDelegate());
        mHolder = new AddStoreViewHolder(view, mPresenter);

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
        mapFragment.getMapAsync(mHolder);
    }

    @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mPresenter.bindView(mHolder);
    }

    @Override public void onDestroyView() {
        mPresenter.unbindView();
        super.onDestroyView();
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_add_store);
    }

    @Override public boolean hasHomeAsUp() {
        return true;
    }

    @Override protected void onEvent(
            String event,
            Object... data
    ) {
        if (EVENT_KEY_START_NEXT.equals(event) && data != null && data.length == 1 &&
            data[0] instanceof StoreModel) {
            actionGoToNext((StoreModel) data[0]);
        }
    }

    private void actionGoToNext(
            StoreModel store
    ) {
        StoreSaveResultEvent event = getArguments().getParcelable(BUNDLE_KEY_DIALOG_RESULT_EVENT);
        NavigationUtil.startContentFragment(getFragmentManager(), AddBannerContentFragment.instantiate(event, store));
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
