package ir.asparsa.hobbytaste.ui.list.provider;

import android.support.annotation.NonNull;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.provider.BaseListProvider;
import ir.asparsa.common.net.dto.StoreDetailsDto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.net.StoreService;
import ir.asparsa.hobbytaste.ui.list.data.GalleryData;
import ir.asparsa.hobbytaste.ui.list.data.RatingData;
import ir.asparsa.hobbytaste.ui.list.data.StoreMapData;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreDetailsProvider extends BaseListProvider<StoreDetailsDto> {

    @Inject
    StoreService mStoreService;

    public StoreDetailsProvider(
            @NonNull RecyclerListAdapter adapter,
            @NonNull BaseRecyclerFragment.OnInsertData insertData,
            @NonNull final StoreModel store
    ) {
        super(adapter, insertData);
        ApplicationLauncher.mainComponent().inject(this);

        insertData.OnDataInserted(false, new ArrayList<BaseRecyclerData>() {{
            add(new StoreMapData(store));
            if (store.getBanners() != null && store.getBanners().size() != 0) {
                add(new GalleryData(store.getBanners()));
            }
            add(new RatingData(store.getRate()));
        }});
    }

    @Override protected boolean isEndOfList(StoreDetailsDto listModel) {
        return true;
    }

    @Override protected List<BaseRecyclerData> convertToListData(
            StoreDetailsDto listModel
    ) {
        List<BaseRecyclerData> list = new ArrayList<>();
//        list.add()
        return list;
    }

    @Override public void provideData(
            long limit,
            long offset
    ) {
//        ConnectableObservable<StoreDetailsDto> observable = getRefreshable();
//        observable.subscribe(this);
//        observable.connect();
    }

    private ConnectableObservable<StoreDetailsDto> getRefreshable(Long id) {
        return mStoreService
                .loadStoreDetailsModels(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .share()
                .replay();
    }

}
