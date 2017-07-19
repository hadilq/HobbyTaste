package ir.asparsa.hobbytaste.ui.fragment.recycler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.route.PlaceRoute;
import ir.asparsa.hobbytaste.core.route.RouteFactory;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.list.data.PlaceData;
import ir.asparsa.hobbytaste.ui.list.holder.PlaceViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.PlacesProvider;
import org.jetbrains.annotations.NotNull;
import rx.functions.Action1;

import javax.inject.Inject;

/**
 * @author hadi
 */
public class PlacesRecyclerFragment extends AbsRecyclerFragment<PlacesProvider> {

    public static final String BUNDLE_KEY_LAT = "BUNDLE_KEY_LAT";
    public static final String BUNDLE_KEY_LNG = "BUNDLE_KEY_LNG";

    @Inject
    AuthorizationManager mAuthorizationManager;
    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    RouteFactory mRouteFactory;

    public static PlacesRecyclerFragment instantiate(Bundle bundle) {
        PlacesRecyclerFragment fragment = new PlacesRecyclerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Override public void onDestroyView() {
        mProvider.clear();
        super.onDestroyView();
    }

    @Override protected PlacesProvider provideDataList(
            RecyclerListAdapter adapter,
            OnInsertData insertData
    ) {
        double lat = getArguments().getDouble(BUNDLE_KEY_LAT);
        double lng = getArguments().getDouble(BUNDLE_KEY_LNG);
        return new PlacesProvider(adapter, insertData, lat, lng);
    }

    @Override protected <T extends BaseViewHolder> Action1<T> getObserver() {
        return new Action1<T>() {
            @Override public void call(T t) {
                if (t instanceof PlaceViewHolder) {
                    ((PlaceViewHolder) t).clickStream().subscribe(getPlaceObserver());
                }
            }
        };
    }

    @NotNull private Action1<PlaceData> getPlaceObserver() {
        return new Action1<PlaceData>() {
            @Override public void call(PlaceData placeData) {
                instantiateStoreDetail(placeData.getStoreModel());
            }
        };
    }

    @Override protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        SparseArrayCompat<Class<? extends BaseViewHolder>> array = super.getViewHoldersList();
        array.put(PlaceData.VIEW_TYPE, PlaceViewHolder.class.asSubclass(BaseViewHolder.class));
        return array;
    }

    public void onInsertNewPlace(StoreModel store) {
        mAdapter.getList().add(0, new PlaceData(store));
        mAdapter.notifyItemInserted(0);
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
