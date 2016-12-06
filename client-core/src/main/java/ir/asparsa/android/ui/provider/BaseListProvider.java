package ir.asparsa.android.ui.provider;

import android.support.annotation.NonNull;
import ir.asparsa.android.core.async.ErrorCallback;
import ir.asparsa.android.core.async.SuccesseCallback;
import ir.asparsa.android.core.model.BaseListModel;
import ir.asparsa.android.core.model.ErrorModel;
import ir.asparsa.android.ui.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.data.BaseRecyclerData;
import ir.asparsa.android.ui.fragment.list.BaseListFragment;
import junit.framework.Assert;

import java.util.List;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public abstract class BaseListProvider<MODEL, ERROR extends ErrorModel>
        implements SuccesseCallback<BaseListModel<MODEL>>, ErrorCallback<ERROR> {

    protected final RecyclerListAdapter mAdapter;
    protected final BaseListFragment.OnInsertData mOnInsertData;

    public BaseListProvider(
            @NonNull RecyclerListAdapter adapter, @NonNull BaseListFragment.OnInsertData insertData) {
        Assert.assertNotNull(adapter);
        Assert.assertNotNull(insertData);
        mAdapter = adapter;
        mOnInsertData = insertData;
    }

    public void onSuccess(BaseListModel<MODEL> listModel) {
        mOnInsertData.OnDataInserted(listModel.isEndOfList(), convertToListData(listModel));
    }

    public void onError(ERROR error) {
        mOnInsertData.onError(error.getLocalizedMessage());
    }

    protected abstract List<BaseRecyclerData> convertToListData(BaseListModel<MODEL> listModel);

    public abstract void provideData(long limit, long offset);
}
