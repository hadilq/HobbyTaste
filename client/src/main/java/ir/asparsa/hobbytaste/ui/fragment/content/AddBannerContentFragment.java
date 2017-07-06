package ir.asparsa.hobbytaste.ui.fragment.content;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.core.util.UiUtil;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.mvp.holder.AddBannerViewHolder;
import ir.asparsa.hobbytaste.ui.mvp.presenter.AddBannerPresenter;

import javax.inject.Inject;

/**
 * Created by hadi
 * on 1/15/2017 AD.
 */
public class AddBannerContentFragment extends BaseContentFragment {

    public static final String BUNDLE_KEY_STORE = "BUNDLE_KEY_STORE";

    public static final String BUNDLE_KEY_DIALOG_RESULT_EVENT = "BUNDLE_KEY_DIALOG_RESULT_EVENT";
    private static final int PICK_IMAGE_REQUEST = 23445;
    public static final String EVENT_KEY_CHOOSE_IMAGE = "EVENT_KEY_CHOOSE_IMAGE";
    public static final String EVENT_KEY_ADD_NEW_BANNER = "EVENT_KEY_ADD_NEW_BANNER";
    public static final String EVENT_KEY_SEND_STORE = "EVENT_KEY_SEND_STORE";

    @Inject
    NavigationUtil mNavigationUtil;

    private AddBannerPresenter mPresenter;
    private AddBannerViewHolder mHolder;

    public static AddBannerContentFragment instantiate(
            AddStoreContentFragment.StoreSaveResultEvent event,
            StoreModel store
    ) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_STORE, store);
        bundle.putParcelable(BUNDLE_KEY_DIALOG_RESULT_EVENT, event);

        AddBannerContentFragment fragment = new AddBannerContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.add_banner_content_fragment, container, false);
        ButterKnife.bind(this, view);

        mPresenter = new AddBannerPresenter(getDelegate());
        mHolder = new AddBannerViewHolder(view, mPresenter);
        return view;
    }

    @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mPresenter.bindView(mHolder);
    }

    @Override public void onDestroyView() {
        mPresenter.unbindView();
        super.onDestroyView();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mPresenter.tryToDeleteFile();
    }

    @Override public boolean hasHomeAsUp() {
        return true;
    }

    @Override public BackState onBackPressed() {
        mPresenter.releaseBanner();
        return super.onBackPressed();
    }

    @Override public void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        L.i(getClass(), "onActivityResult gets called");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null &&
            data.getData() != null) {
            mPresenter.prepareBanner(data.getData());
        }
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_add_banner);
    }

    @Override protected void onEvent(
            String event,
            Object... data
    ) {
        if (EVENT_KEY_CHOOSE_IMAGE.equals(event) && data != null && data.length == 1 &&
            data[0] instanceof Intent) {
            startActivityForResult(
                    Intent.createChooser((Intent) data[0], "Select Picture"), PICK_IMAGE_REQUEST);
        } else if (EVENT_KEY_ADD_NEW_BANNER.equals(event) && data != null && data.length == 2 &&
                   data[0] instanceof AddStoreContentFragment.StoreSaveResultEvent &&
                   data[1] instanceof StoreModel) {
            mNavigationUtil.startContentFragment(getFragmentManager(), AddBannerContentFragment
                    .instantiate((AddStoreContentFragment.StoreSaveResultEvent) data[0], (StoreModel) data[1]));
        } else if (EVENT_KEY_SEND_STORE.equals(event) && data != null && data.length == 1 &&
                   data[0] instanceof StoreModel) {
            sendEvent((StoreModel) data[0]);
        }
    }

    private void sendEvent(StoreModel storeModel) {
        AddStoreContentFragment.StoreSaveResultEvent event = getArguments()
                .getParcelable(AddStoreContentFragment.BUNDLE_KEY_DIALOG_RESULT_EVENT);
        event.setStoreModel(storeModel);

        UiUtil.invokeEventReceiver(event, getFragmentManager(), true);
    }
}
