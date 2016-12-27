package ir.asparsa.hobbytaste.ui.fragment.recycler;

import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.list.data.CommentData;
import ir.asparsa.hobbytaste.ui.list.data.GalleryData;
import ir.asparsa.hobbytaste.ui.list.data.RatingData;
import ir.asparsa.hobbytaste.ui.list.data.StoreMapData;
import ir.asparsa.hobbytaste.ui.list.holder.CommentViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.GalleryViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.RatingViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.StoreMapViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.StoreDetailsProvider;
import junit.framework.Assert;
import rx.Observer;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreDetailsRecyclerFragment extends BaseRecyclerFragment<StoreDetailsProvider> {

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
        array.put(GalleryData.VIEW_TYPE, GalleryViewHolder.class.asSubclass(BaseViewHolder.class));
        array.put(RatingData.VIEW_TYPE, RatingViewHolder.class.asSubclass(BaseViewHolder.class));
        array.put(CommentData.VIEW_TYPE, CommentViewHolder.class.asSubclass(BaseViewHolder.class));
        return array;
    }

    @Override
    protected StoreDetailsProvider provideDataList(
            RecyclerListAdapter adapter,
            OnInsertData insertData
    ) {
        StoreModel store = getArguments().getParcelable(BUNDLE_KEY_STORE);
        if (store == null) {
            store = new StoreModel();
            Assert.fail("Store cannot be null!");
        }
        return new StoreDetailsProvider(adapter, insertData, store);
    }

    @Override protected <T extends Event> Observer<T> getObserver() {
        return new Observer<T>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(T t) {
            }
        };
    }

    public void addComment(CommentModel comment) {
        mProvider.addComment(comment);
    }
}
