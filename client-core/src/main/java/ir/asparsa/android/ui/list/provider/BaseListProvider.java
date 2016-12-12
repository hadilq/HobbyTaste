package ir.asparsa.android.ui.list.provider;

import android.support.annotation.NonNull;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import junit.framework.Assert;
import rx.Subscriber;

import java.util.List;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public abstract class BaseListProvider<MODEL> extends Subscriber<MODEL> {

    protected final RecyclerListAdapter mAdapter;
    protected final BaseRecyclerFragment.OnInsertData mOnInsertData;

    public BaseListProvider(
            @NonNull RecyclerListAdapter adapter, @NonNull BaseRecyclerFragment.OnInsertData insertData) {
        Assert.assertNotNull(adapter);
        Assert.assertNotNull(insertData);
        mAdapter = adapter;
        mOnInsertData = insertData;
    }

    @Override public void onError(Throwable error) {
        mOnInsertData.onError(error.getLocalizedMessage());
    }

    @Override public void onNext(MODEL listModel) {
        mOnInsertData.OnDataInserted(isEndOfList(listModel), convertToListData(listModel));
    }

    @Override public void onCompleted() {
    }

    protected abstract boolean isEndOfList(MODEL listModel);

    protected abstract List<BaseRecyclerData> convertToListData(MODEL listModel);

    public abstract void provideData(long limit, long offset);
}
