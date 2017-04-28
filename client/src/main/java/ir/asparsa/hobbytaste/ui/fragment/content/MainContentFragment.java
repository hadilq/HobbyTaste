package ir.asparsa.hobbytaste.ui.fragment.content;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.maps.SupportMapFragment;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.route.PlaceRoute;
import ir.asparsa.hobbytaste.core.route.RouteFactory;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.mvp.holder.MainContentViewHolder;
import ir.asparsa.hobbytaste.ui.mvp.presenter.StorePresenter;
import ir.asparsa.hobbytaste.ui.wrapper.WMap;

import javax.inject.Inject;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class MainContentFragment extends BaseContentFragment {

    public static final String EVENT_KEY_INSTANTIATE_STORE_DETAIL = "EVENT_KEY_INSTANTIATE_STORE_DETAIL";

    @Inject
    RouteFactory mRouteFactory;

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
        setHasOptionsMenu(true);
        mPresenter = new StorePresenter(getDelegate());
        mHolder = new MainContentViewHolder(mPresenter);
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

    @Override public void onStart() {
        super.onStart();
        mPresenter.bindView(mHolder);
    }

    @Override public void onDestroyView() {
        mPresenter.unbindView();
        super.onDestroyView();
    }

    @Override public void onCreateOptionsMenu(
            Menu menu,
            MenuInflater inflater
    ) {
        inflater.inflate(R.menu.menu_share, menu);
        MenuItem share = menu.findItem(R.id.share);
        share.getIcon().mutate()
             .setColorFilter(getResources().getColor(R.color.background), PorterDuff.Mode.SRC_ATOP);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        getString(R.string.share_link)
                );
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                WMap map = mHolder.getMap();
                if (map == null) {
                    return;
                }
                NavigationUtil.startContentFragment(
                        getFragmentManager(), AddStoreContentFragment.instantiate(
                                map.getCameraPosition().getRealCameraPosition(),
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

    @Override protected void onEvent(
            String event,
            Object... data
    ) {
        if (data != null && data.length == 1 && data[0] instanceof StoreModel) {
            instantiateStoreDetail((StoreModel) data[0]);
        }
    }

    private void instantiateStoreDetail(@NonNull StoreModel storeModel) {
        Uri.Builder uriBuilder = mRouteFactory.getInternalUriBuilder(getResources(), PlaceRoute.class);
        if (uriBuilder == null) {
            return;
        }
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                uriBuilder.appendPath(Long.toString(storeModel.getHashCode())).build()
        ));
    }
}
