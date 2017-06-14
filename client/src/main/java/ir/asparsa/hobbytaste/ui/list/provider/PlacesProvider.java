package ir.asparsa.hobbytaste.ui.list.provider;

import android.content.Context;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.data.DataObserver;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.list.data.PlaceData;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 12/14/2016 AD.
 */
public class PlacesProvider extends AbsListProvider implements Observer<StoresManager.StoresResult> {

    private static final int STORES_LIMIT = 20;

    @Inject
    StoresManager mStoresManager;
    @Inject
    Context mContext;

    private final CompositeSubscription mSubscription = new CompositeSubscription();
    private final double mLatitude;
    private final double mLongitude;

    public PlacesProvider(
            RecyclerListAdapter adapter,
            BaseRecyclerFragment.OnInsertData onInsertData,
            double lat,
            double lng
    ) {
        super(adapter, onInsertData);
        ApplicationLauncher.mainComponent().inject(this);
        this.mLatitude = lat;
        this.mLongitude = lng;
    }

    @Override public void provideData(
            long offset,
            int limit
    ) {
        StoresManager.Constraint constraint = new StoresManager.Constraint(
                mLatitude, mLongitude, (int) offset, STORES_LIMIT);
        mSubscription.add(mStoresManager.loadStores(constraint, this));
    }

    public void clear() {
        mSubscription.clear();
    }

    @Override public void onCompleted() {
    }

    @Override public void onError(Throwable e) {
        L.e(getClass(), "An error happened!", e);
        mOnInsertData.onError(e.getLocalizedMessage());
    }

    @Override public void onNext(StoresManager.StoresResult storesResult) {
        final List<BaseRecyclerData> stores = new ArrayList<>();
        for (StoreModel store : storesResult.getStores()) {
            stores.add(new PlaceData(store));
        }
        mOnInsertData.OnDataInserted(new DataObserver(storesResult.getTotalElements()) {
            @Override public void onCompleted() {
                for (; index < stores.size(); index++) {
                    deque.add(stores.get(index));
                    mAdapter.notifyItemInserted(deque.size() - 1);
                }
            }

            @Override public void onNext(BaseRecyclerData baseRecyclerData) {
                if (stores.contains(baseRecyclerData)) {
                    stores.remove(baseRecyclerData);
                }
                deque.add(baseRecyclerData);
            }
        });
    }
}
