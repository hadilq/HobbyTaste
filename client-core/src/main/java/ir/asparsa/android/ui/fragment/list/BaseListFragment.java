package ir.asparsa.android.ui.fragment.list;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.R;
import ir.asparsa.android.R2;
import ir.asparsa.android.ui.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.data.BaseRecyclerData;
import ir.asparsa.android.ui.data.TryAgainData;
import ir.asparsa.android.ui.fragment.BaseFragment;
import ir.asparsa.android.ui.holder.BaseViewHolder;
import ir.asparsa.android.ui.holder.TryAgainViewHolder;
import ir.asparsa.android.ui.provider.BaseListProvider;
import ir.asparsa.android.ui.view.TryAgainView;

import java.util.List;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public abstract class BaseListFragment extends BaseFragment {

    private static final String BUNDLE_KEY_SCROLL_POSITION = "BUNDLE_KEY_SCROLL_POSITION";
    private static final String BUNDLE_KEY_NEXT_OFFSET = "BUNDLE_KEY_NEXT_OFFSET";
    private static final long LIMIT_DEFAULT = 20;

    private RecyclerListAdapter mAdapter;
    private BaseListProvider mProvider;
    private LinearLayoutManager mLayoutManager;
    private TryAgainView.OnTryAgainListener mOnTryAgainListener;

    private final Handler mHandler = new Handler();
    private long mLimit = LIMIT_DEFAULT;
    private long mNextOffset = 0;
    private boolean mLoading = true;
    private int mScrollPosition;

    @BindView(R2.id.list)
    RecyclerView mRecyclerView;
    @BindView(R2.id.try_again)
    TryAgainView mTryAgainView;


    @Nullable @Override public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.base_list_fragment, container, false);

        ButterKnife.bind(this, rootView);

        mTryAgainView.setExtraView(getEmptyView());
        mOnTryAgainListener = new TryAgainView.OnTryAgainListener() {
            @Override public void tryAgain() {
                provideDataAndStart();
            }
        };
        mTryAgainView.setTryAgainListener(mOnTryAgainListener);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new LoadingListener());

        mAdapter = new RecyclerListAdapter(getViewHoldersList(), getItemClickListener());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mProvider = provideDataList(mAdapter, new OnInsertData());
        return rootView;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNextOffset = 0;
        mScrollPosition = getArguments().getInt(BUNDLE_KEY_SCROLL_POSITION, 0);
        long limit = getArguments().getLong(BUNDLE_KEY_NEXT_OFFSET, -1L);
        if (limit > 0) {
            mLimit = limit;
        }
        provideDataAndStart();
    }

    @Override public void onDestroyView() {
        getArguments().putInt(BUNDLE_KEY_SCROLL_POSITION, mLayoutManager.findFirstVisibleItemPosition());
        getArguments().putLong(BUNDLE_KEY_NEXT_OFFSET, mNextOffset);
        super.onDestroyView();
    }

    private void provideDataAndStart() {
        provideData();
        mTryAgainView.start();
    }

    private void provideData() {
        mProvider.provideData(mLimit, mNextOffset);
        mNextOffset += mLimit;
    }

    protected abstract View getEmptyView();

    protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        return new SparseArrayCompat<Class<? extends BaseViewHolder>>() {{
            put(TryAgainData.VIEW_TYPE, TryAgainViewHolder.class.asSubclass(BaseViewHolder.class));
        }};
    }

    protected abstract BaseListProvider provideDataList(RecyclerListAdapter adapter, OnInsertData insertData);

    protected abstract OnItemClickListener getItemClickListener();

    private class LoadingListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0) { //check for scroll down
                checkIfReadyToProvide();
            }
        }
    }

    private void checkIfReadyToProvide() {
        int totalItemCount = mLayoutManager.getItemCount();
        int visibleItemCount = mLayoutManager.getChildCount();
        int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

        if (mLoading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
            mLoading = false;
            provideData();
        }
    }

    public class OnInsertData {
        public void OnDataInserted(boolean endOfList, @NonNull List<? extends BaseRecyclerData> data) {
            if (mAdapter.isEmpty() && data.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mTryAgainView.showExtraView();
            } else {
                if (mAdapter.isEmpty()) {
                    mTryAgainView.finish();
                }
                if (mLimit > LIMIT_DEFAULT) {
                    mLimit = LIMIT_DEFAULT;
                }
                mRecyclerView.setVisibility(View.VISIBLE);
                List<BaseRecyclerData> list = mAdapter.getList();
                if (!list.isEmpty() && list.get(list.size() - 1).getViewType() == TryAgainData.VIEW_TYPE) {
                    list.remove(list.size() - 1);
                }
                list.addAll(data);
                mAdapter.notifyDataSetChanged();
                mLoading = !endOfList;
                if (mLoading) {
                    list.add(new TryAgainData(true, mOnTryAgainListener));
                    mHandler.post(new Runnable() {
                        @Override public void run() {
                            checkIfReadyToProvide();
                        }
                    });
                }
                if (mScrollPosition > 0) {
                    // No need of scroll position any more
                    mScrollPosition = -1;
                    mHandler.post(new Runnable() {
                        @Override public void run() {
                            mLayoutManager.scrollToPosition(mScrollPosition);
                        }
                    });
                }
            }
        }

        public void onError(String message) {
            List<BaseRecyclerData> list = mAdapter.getList();
            if (list.isEmpty()) {
                mTryAgainView.onError(message);
            } else {
                BaseRecyclerData data = list.get(list.size() - 1);
                if (data instanceof TryAgainData) {
                    TryAgainData tryAgainData = (TryAgainData) data;
                    tryAgainData.setErrorMessage(message);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, BaseRecyclerData data, int position);
    }

}
