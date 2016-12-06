package ir.asparsa.android.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.asparsa.android.ui.data.BaseRecyclerData;
import ir.asparsa.android.ui.fragment.list.BaseListFragment;
import ir.asparsa.android.ui.holder.BaseViewHolder;
import ir.asparsa.android.ui.holder.EmptyViewHolder;
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

    private List<BaseRecyclerData> list = new ArrayList<>();
    private final SparseArrayCompat<Class<? extends BaseViewHolder>> mHoldersMap;
    private final BaseListFragment.OnItemClickListener mOnClickListener;

    public RecyclerListAdapter(
            @NonNull SparseArrayCompat<Class<? extends BaseViewHolder>> holdersMap,
            @Nullable BaseListFragment.OnItemClickListener onClickListener) {
        this.mHoldersMap = holdersMap;
        this.mOnClickListener = onClickListener;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = null;
        for (int pos = 0; pos < mHoldersMap.size(); pos++) {
            int key = mHoldersMap.keyAt(pos);
            if (key == viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

                Class<? extends BaseViewHolder> clazz = mHoldersMap.get(key);
                Constructor<? extends BaseViewHolder> constructor;
                try {
                    constructor = clazz.getConstructor(View.class);
                    baseViewHolder = constructor.newInstance(view);
                } catch (NoSuchMethodException e) {
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
            baseViewHolder = new EmptyViewHolder(new View(parent.getContext()));
        }
        return baseViewHolder;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BaseViewHolder) {
            BaseViewHolder baseHolder = (BaseViewHolder) holder;
            final BaseRecyclerData data = list.get(position);
            if (mOnClickListener != null) {
                baseHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
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

    @Override public int getItemCount() {
        return list.size();
    }

    @Override public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }

    public List<BaseRecyclerData> getList() {
        return list;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
