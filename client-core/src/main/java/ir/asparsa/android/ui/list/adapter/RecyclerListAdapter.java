package ir.asparsa.android.ui.list.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.holder.BaseViewHolderFactory;
import ir.asparsa.android.ui.list.holder.EmptyViewHolder;
import junit.framework.Assert;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public class RecyclerListAdapter extends RecyclerView.Adapter {

    private final Bundle mSavedInstanceState;
    private final LinearLayoutManager mLayoutManager;
    private final RecyclerView mRecyclerView;
    private ArrayList<BaseRecyclerData> mList = new ArrayList<>();
    private final SparseArrayCompat<Class<? extends BaseViewHolder>> mHoldersMap;
    private final Action1<BaseViewHolder> mObserver;
    private final BaseViewHolderFactory mFactory;

    public RecyclerListAdapter(
            @NonNull RecyclerView recyclerView,
            @NonNull LinearLayoutManager layoutManager,
            @Nullable Bundle savedInstanceState,
            @NonNull SparseArrayCompat<Class<? extends BaseViewHolder>> holdersMap,
            @NonNull BaseViewHolderFactory factory,
            @NonNull Action1<BaseViewHolder> observer
    ) {
        this.mRecyclerView = recyclerView;
        this.mLayoutManager = layoutManager;
        this.mHoldersMap = holdersMap;
        this.mFactory = factory;
        this.mObserver = observer;
        this.mSavedInstanceState = savedInstanceState;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType
    ) {
        BaseViewHolder baseViewHolder = null;
        for (int pos = 0; pos < mHoldersMap.size(); pos++) {
            int key = mHoldersMap.keyAt(pos);
            if (key == viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

                baseViewHolder = mFactory.create(mHoldersMap.get(key), view);
                break;
            }
        }

        if (baseViewHolder == null) {
            // Shouldn't happen
            L.w(this.getClass(), "baseViewHolder becomes null!");
            baseViewHolder = new EmptyViewHolder(new View(parent.getContext()));
        }
        mObserver.call(baseViewHolder);
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(
            RecyclerView.ViewHolder holder,
            final int position
    ) {
        if (holder instanceof BaseViewHolder) {
            BaseViewHolder baseHolder = (BaseViewHolder) holder;
            final BaseRecyclerData data = mList.get(position);

            baseHolder.onCreate(mSavedInstanceState);
            onBindData(baseHolder, data);
        } else {
            Assert.fail("All view holders must be extended of BaseViewHolder");
        }
    }

    @SuppressWarnings("unchecked")// The type is right by generic types
    private void onBindData(
            BaseViewHolder baseHolder,
            BaseRecyclerData data
    ) {
        baseHolder.onBindView(data);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getViewType();
    }

    public ArrayList<BaseRecyclerData> getList() {
        return mList;
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    public void onResume() {
        for (int i = 0; i < getItemCount(); ++i) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof BaseViewHolder) {
                ((BaseViewHolder) holder).onResume();
            }
        }
    }

    public void onStart() {
        for (int i = 0; i < getItemCount(); ++i) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof BaseViewHolder) {
                ((BaseViewHolder) holder).onStart();
            }
        }
    }

    public void onPause() {
        for (int i = 0; i < getItemCount(); ++i) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof BaseViewHolder) {
                ((BaseViewHolder) holder).onPause();
            }
        }
    }

    public void onStop() {
        for (int i = 0; i < getItemCount(); ++i) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof BaseViewHolder) {
                ((BaseViewHolder) holder).onStop();
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        for (int i = 0; i < getItemCount(); ++i) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof BaseViewHolder) {
                ((BaseViewHolder) holder).onSaveInstanceState(outState);
            }
        }
    }

    public void onDestroy() {
        for (int i = 0; i < getItemCount(); ++i) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof BaseViewHolder) {
                ((BaseViewHolder) holder).onDestroy();
            }
        }
    }

    public void onLowMemory() {
        for (int i = 0; i < getItemCount(); ++i) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof BaseViewHolder) {
                ((BaseViewHolder) holder).onLowMemory();
            }
        }
    }

    public List<BaseRecyclerData> findData(Class<? extends BaseRecyclerData> clazz) {
        List<BaseRecyclerData> list = new ArrayList<>();
        for (BaseRecyclerData data : mList) {
            if (clazz.isInstance(data)) {
                list.add(data);
            }
        }
        return list;
    }
}
