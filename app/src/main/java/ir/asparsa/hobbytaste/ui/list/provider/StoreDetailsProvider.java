package ir.asparsa.hobbytaste.ui.list.provider;

import android.support.annotation.NonNull;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.provider.BaseListProvider;
import ir.asparsa.common.net.dto.StoreDto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.net.StoreService;
import ir.asparsa.hobbytaste.ui.list.data.GalleryData;
import ir.asparsa.hobbytaste.ui.list.data.RatingData;
import ir.asparsa.hobbytaste.ui.list.data.StoreMapData;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreDetailsProvider extends BaseListProvider<StoreDto> {

    @Inject
    StoreService mStoreService;

    private final StoreModel mStore;

    public StoreDetailsProvider(
            @NonNull RecyclerListAdapter adapter,
            @NonNull BaseRecyclerFragment.OnInsertData insertData,
            @NonNull final StoreModel store
    ) {
        super(adapter, insertData);
        ApplicationLauncher.mainComponent().inject(this);
        this.mStore = store;

        mOnInsertData.OnDataInserted(false, new ArrayList<BaseRecyclerData>() {{
            add(new StoreMapData(mStore));
            if (mStore.getBanners() != null && mStore.getBanners().size() != 0) {
                add(new GalleryData(mStore.getBanners()));
            }
            add(new RatingData(mStore.getRate()));
        }});
    }

    @Override protected boolean isEndOfList(StoreDto listModel) {
        return true;
    }

    @Override protected List<BaseRecyclerData> convertToListData(
            StoreDto listModel
    ) {
        List<BaseRecyclerData> list = new ArrayList<>();
//        list.add()
        return list;
    }

    @Override public void provideData(
            long limit,
            long offset
    ) {

    }

}
