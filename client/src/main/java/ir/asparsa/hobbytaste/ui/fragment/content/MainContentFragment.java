package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.mvp.holder.MainContentViewHolder;
import ir.asparsa.hobbytaste.ui.mvp.presenter.StorePresenter;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class MainContentFragment extends BaseContentFragment {

    private MainContentViewHolder mHolder;
    private StorePresenter mPresenter;

    public static MainContentFragment instantiate() {
        MainContentFragment fragment = new MainContentFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
        mPresenter = new StorePresenter(this);
        mHolder = new MainContentViewHolder(mPresenter);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fragment fragment = NavigationUtil.getActiveFragment(getChildFragmentManager());
        SupportMapFragment mapFragment;
        if (!(fragment instanceof SupportMapFragment)) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                                     .replace(R.id.content_nested, mapFragment)
                                     .commit();
            mapFragment.getMapAsync(mHolder);
        } else {
            ((SupportMapFragment) fragment).getMapAsync(mHolder);
        }
    }

    @Override public void onStart() {
        super.onStart();
        mPresenter.bindView(mHolder);
    }

    @Override public void onDestroyView() {
        mPresenter.unbindView();
        super.onDestroyView();
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.app_name);
    }

    @Override public BackState onBackPressed() {
        return BackState.CLOSE_APP;
    }

    @Override public FloatingActionButtonObserver getFloatingActionButtonObserver() {
        return new FloatingActionButtonObserver() {
            @Override public void onNext(View view) {
                GoogleMap map = mHolder.getMap();
                if (map == null) {
                    return;
                }
                NavigationUtil.startContentFragment(
                        getFragmentManager(), AddStoreContentFragment.instantiate(
                                map.getCameraPosition(),
                                new AddStoreContentFragment.StoreSaveResultEvent(getTagName())));
            }
        };
    }

    @Override public void onEvent(BaseEvent event) {
        if (event instanceof AddStoreContentFragment.StoreSaveResultEvent) {
            AddStoreContentFragment.StoreSaveResultEvent result = (AddStoreContentFragment.StoreSaveResultEvent) event;
            L.i(MainContentFragment.class, "Added store: " + result.getStoreModel());
            mPresenter.onNewStore(result.getStoreModel());
        }
    }

    public void instantiateStoreDetail(@NonNull StoreModel storeModel) {
        NavigationUtil.startContentFragment(
                getFragmentManager(),
                StoreDetailsContentFragment.instantiate(storeModel)
        );
    }
}
