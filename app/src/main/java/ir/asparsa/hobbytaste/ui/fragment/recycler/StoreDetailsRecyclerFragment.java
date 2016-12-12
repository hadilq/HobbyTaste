package ir.asparsa.hobbytaste.ui.fragment.recycler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.provider.BaseListProvider;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.list.data.StoreMapData;
import ir.asparsa.hobbytaste.ui.list.holder.StoreMapViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.StoreDetailsProvider;
import junit.framework.Assert;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreDetailsRecyclerFragment extends BaseRecyclerFragment {

    public static final String BUNDLE_KEY_STORE = "BUNDLE_KEY_STORE";

    public static StoreDetailsRecyclerFragment instantiate(Bundle bundle) {

        StoreDetailsRecyclerFragment fragment = new StoreDetailsRecyclerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View getEmptyView() {
        return null;
    }

    @Override
    protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        SparseArrayCompat<Class<? extends BaseViewHolder>> array = super.getViewHoldersList();
        array.put(StoreMapData.VIEW_TYPE, StoreMapViewHolder.class.asSubclass(BaseViewHolder.class));
        return array;
    }

    @Override
    protected BaseListProvider provideDataList(
            RecyclerListAdapter adapter, OnInsertData insertData) {
        StoreModel store = getArguments().getParcelable(BUNDLE_KEY_STORE);
        if (store == null) {
            store = new StoreModel();
            Assert.fail("Store cannot be null!");
        }
        return new StoreDetailsProvider(adapter, insertData, store);
    }

    @Override
    protected OnItemClickListener getItemClickListener() {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(View view, BaseRecyclerData data, int position) {

            }
        };
    }
}
