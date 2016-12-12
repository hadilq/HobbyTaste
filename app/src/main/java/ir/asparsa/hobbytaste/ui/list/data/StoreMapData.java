package ir.asparsa.hobbytaste.ui.list.data;

import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.StoreModel;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreMapData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.store_map;

    private StoreModel store;

    public StoreMapData(StoreModel store) {
        this.store = store;
    }

    public StoreModel getStore() {
        return store;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }
}
