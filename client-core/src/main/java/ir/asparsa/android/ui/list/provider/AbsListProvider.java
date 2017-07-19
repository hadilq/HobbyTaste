package ir.asparsa.android.ui.list.provider;

import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import junit.framework.Assert;
import rx.subscriptions.CompositeSubscription;

/**
 * @author hadi
 */
public abstract class AbsListProvider {

    protected final RecyclerListAdapter mAdapter;
    protected final BaseRecyclerFragment.OnInsertData mOnInsertData;
    protected final CompositeSubscription mSubscription = new CompositeSubscription();

    public AbsListProvider(
            RecyclerListAdapter adapter,
            BaseRecyclerFragment.OnInsertData onInsertData
    ) {
        Assert.assertNotNull(adapter);
        Assert.assertNotNull(onInsertData);
        this.mAdapter = adapter;
        this.mOnInsertData = onInsertData;
    }

    public abstract void provideData(
            long offset,
            int limit
    );

    public void clear() {
        mSubscription.clear();
    }
}
