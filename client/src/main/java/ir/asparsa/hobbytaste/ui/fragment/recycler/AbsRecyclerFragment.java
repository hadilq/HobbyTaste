package ir.asparsa.hobbytaste.ui.fragment.recycler;

import android.support.annotation.Nullable;
import android.view.View;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolderFactory;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.hobbytaste.ui.list.holder.ViewHolderFactory;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;

/**
 * @author hadi
 */
public abstract class AbsRecyclerFragment<P extends AbsListProvider> extends BaseRecyclerFragment<P> {

    @Inject
    ViewHolderFactory mViewHolderFactory;

    protected final CompositeSubscription mSubscription = new CompositeSubscription();

    @Override protected BaseViewHolderFactory getViewHolderFactory() {
        return mViewHolderFactory;
    }

    @Override public void onDestroyView() {
        mProvider.clear();
        mSubscription.clear();
        super.onDestroyView();
    }

    @Nullable @Override protected View getEmptyView() {
        return null;
    }

}
