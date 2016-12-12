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
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.holder.EmptyViewHolder;
import junit.framework.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    private List<BaseRecyclerData> list = new ArrayList<>();
    private final SparseArrayCompat<Class<? extends BaseViewHolder>> mHoldersMap;
    private final BaseRecyclerFragment.OnItemClickListener mOnClickListener;

    public RecyclerListAdapter(
            @NonNull RecyclerView recyclerView,
            @NonNull LinearLayoutManager layoutManager,
            @Nullable Bundle savedInstanceState,
            @NonNull SparseArrayCompat<Class<? extends BaseViewHolder>> holdersMap,
            @Nullable BaseRecyclerFragment.OnItemClickListener onClickListener) {
        this.mRecyclerView = recyclerView;
        this.mLayoutManager = layoutManager;
        this.mHoldersMap = holdersMap;
        this.mOnClickListener = onClickListener;
        this.mSavedInstanceState = savedInstanceState;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = null;
        for (int pos = 0; pos < mHoldersMap.size(); pos++) {
            int key = mHoldersMap.keyAt(pos);
            if (key == viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

                Class<? extends BaseViewHolder> clazz = mHoldersMap.get(key);
                Constructor<? extends BaseViewHolder> constructor;
                try {
                    constructor = clazz.getConstructor(View.class, Bundle.class);
                    baseViewHolder = constructor.newInstance(view, mSavedInstanceState);
                } catch (NoSuchMethodException e) {
                    L.e(this.getClass(), "View Holder don't have necessary constructor: " + clazz.getName());
                    e.printStackTrace();
                    continue;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    continue;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    continue;
                }
                break;
            }
        }

        if (baseViewHolder == null) {
            // Shouldn't happen
            L.w(this.getClass(), "baseViewHolder become null!");
            baseViewHolder = new EmptyViewHolder(new View(parent.getContext()), mSavedInstanceState);
        }
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BaseViewHolder) {
            BaseViewHolder baseHolder = (BaseViewHolder) holder;
            final BaseRecyclerData data = list.get(position);
            if (mOnClickListener != null) {
                baseHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickListener.onItemClick(v, data, position);
                    }
                });
            } else {
                baseHolder.itemView.setOnClickListener(null);
            }

            onBindData(baseHolder, data);
        } else {
            Assert.fail("All view holders must be extended of BaseViewHolder");
        }
    }

    @SuppressWarnings("unchecked")// The type is right by generic types
    private void onBindData(BaseViewHolder baseHolder, BaseRecyclerData data) {
        baseHolder.onBindView(data);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }

    public List<BaseRecyclerData> getList() {
        return list;
    }

    public boolean isEmpty() {
        return list.isEmpty();
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
}
