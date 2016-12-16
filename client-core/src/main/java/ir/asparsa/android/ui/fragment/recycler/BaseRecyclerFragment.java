package ir.asparsa.android.ui.fragment.recycler;

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
import ir.asparsa.android.ui.fragment.BaseFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.data.TryAgainData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.holder.TryAgainViewHolder;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.android.ui.view.TryAgainView;

import java.util.List;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public abstract class BaseRecyclerFragment extends BaseFragment {

    private static final String BUNDLE_KEY_SCROLL_POSITION = "BUNDLE_KEY_SCROLL_POSITION";
    private static final String BUNDLE_KEY_NEXT_OFFSET = "BUNDLE_KEY_NEXT_OFFSET";
    private static final long LIMIT_DEFAULT = 20;

    protected RecyclerListAdapter mAdapter;
    private AbsListProvider mProvider;
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


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View rootView = inflater.inflate(R.layout.base_list_fragment, container, false);

        ButterKnife.bind(this, rootView);

        mTryAgainView.setExtraView(getEmptyView());
        mOnTryAgainListener = new TryAgainView.OnTryAgainListener() {
            @Override
            public void tryAgain() {
                provideDataAndStart();
            }
        };
        mTryAgainView.setTryAgainListener(mOnTryAgainListener);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new LoadingListener());

        mAdapter = new RecyclerListAdapter(
                mRecyclerView, mLayoutManager, savedInstanceState, getViewHoldersList(), getOnEventListener());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mProvider = provideDataList(mAdapter, new OnInsertData());
        return rootView;
    }

    @Override
    public void onViewCreated(
            View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        mNextOffset = 0;
        mScrollPosition = getArguments().getInt(BUNDLE_KEY_SCROLL_POSITION, 0);
        long limit = getArguments().getLong(BUNDLE_KEY_NEXT_OFFSET, -1L);
        if (limit > 0) {
            mLimit = limit;
        }
        provideDataAndStart();
    }

    @Override
    public void onDestroyView() {
        getArguments().putInt(BUNDLE_KEY_SCROLL_POSITION, mLayoutManager.findFirstVisibleItemPosition());
        getArguments().putLong(BUNDLE_KEY_NEXT_OFFSET, mNextOffset);
        super.onDestroyView();
    }

    private void provideDataAndStart() {
        provideData();
        if (mAdapter.isEmpty()) {
            mTryAgainView.start();
        }
    }

    private void provideData() {
        mProvider.provideData(mLimit, mNextOffset);
        mNextOffset += mLimit;
    }

    @Nullable
    protected abstract View getEmptyView();

    protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        return new SparseArrayCompat<Class<? extends BaseViewHolder>>() {{
            put(TryAgainData.VIEW_TYPE, TryAgainViewHolder.class.asSubclass(BaseViewHolder.class));
        }};
    }

    protected abstract AbsListProvider provideDataList(
            RecyclerListAdapter adapter,
            OnInsertData insertData
    );

    protected abstract OnEventListener getOnEventListener();

    private class LoadingListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(
                RecyclerView recyclerView,
                int dx,
                int dy
        ) {
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
        public void OnDataInserted(
                boolean endOfList,
                @NonNull List<? extends BaseRecyclerData> data
        ) {
            if (mAdapter.isEmpty() && data.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mTryAgainView.showExtraView();
            } else {
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
                        @Override
                        public void run() {
                            checkIfReadyToProvide();
                        }
                    });
                }
                if (mScrollPosition > 0) {
                    // No need of scroll position any more
                    mScrollPosition = -1;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLayoutManager.scrollToPosition(mScrollPosition);
                        }
                    });
                }
                if (!mAdapter.isEmpty()) {
                    mTryAgainView.finish();
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

    public interface OnEventListener {

        void onEvent(
                int subscriber,
                @Nullable Bundle bundle
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.onStart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            mAdapter.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mAdapter != null) {
            mAdapter.onLowMemory();
        }
    }
}
