package ir.asparsa.android.ui.fragment.recycler;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.R;
import ir.asparsa.android.R2;
import ir.asparsa.android.ui.fragment.BaseFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.data.DataObserver;
import ir.asparsa.android.ui.list.data.TryAgainData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.holder.BaseViewHolderFactory;
import ir.asparsa.android.ui.list.holder.TryAgainViewHolder;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.android.ui.view.TryAgainView;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public abstract class BaseRecyclerFragment<P extends AbsListProvider> extends BaseFragment {

    private static final String BUNDLE_KEY_SCROLL_POSITION = "BUNDLE_KEY_SCROLL_POSITION";
    private static final String BUNDLE_KEY_NEXT_OFFSET = "BUNDLE_KEY_NEXT_OFFSET";
    private static final String BUNDLE_KEY_LIMIT = "BUNDLE_KEY_LIMIT";
    private static final String BUNDLE_KEY_LIST = "BUNDLE_KEY_LIST";
    private static final int LIMIT_DEFAULT = 10;

    protected RecyclerListAdapter mAdapter;
    protected P mProvider;
    protected Action1<BaseViewHolder> mContentObserver;
    private LinearLayoutManager mLayoutManager;
    private TryAgainView.OnTryAgainListener mOnTryAgainListener;

    private final Handler mHandler = new Handler();
    private int mLimit = LIMIT_DEFAULT;
    private long mNextOffset = 0;
    private long mLastTriedOffset = -1;
    private long mHeaderSize = 0;
    private boolean mLoading = true;
    private int mScrollPosition;


    @BindView(R2.id.list)
    RecyclerView mRecyclerView;
    @BindView(R2.id.try_again)
    TryAgainView mTryAgainView;
    @BindView(R2.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

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
                refresh();
            }
        };
        mTryAgainView.setTryAgainListener(mOnTryAgainListener);

        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                refresh();
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new LoadingListener());

        final Action1<BaseViewHolder> observer = getObserver();
        mAdapter = new RecyclerListAdapter(
                mRecyclerView, mLayoutManager, savedInstanceState, getViewHoldersList(), getViewHolderFactory(),
                getObserver(observer));

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    @NotNull private Action1<BaseViewHolder> getObserver(final Action1<BaseViewHolder> observer) {
        return new Action1<BaseViewHolder>() {
            @Override public void call(BaseViewHolder holder) {
                if (holder instanceof TryAgainViewHolder) {
                    ((TryAgainViewHolder) holder).clickStream().subscribe(getTryAgainObserver());
                } else {
                    observer.call(holder);
                }
            }
        };
    }

    protected abstract BaseViewHolderFactory getViewHolderFactory();

    @NotNull private Action1<TryAgainData> getTryAgainObserver() {
        return new Action1<TryAgainData>() {
            @Override public void call(TryAgainData tryAgainData) {
                mOnTryAgainListener.tryAgain();
            }
        };
    }

    private void refresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        mLastTriedOffset = mNextOffset - mLimit;
        provideDataAndStart();
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNextOffset = getArguments().getLong(BUNDLE_KEY_NEXT_OFFSET, 0);
        mScrollPosition = getArguments().getInt(BUNDLE_KEY_SCROLL_POSITION, 0);
        mLimit = getArguments().getInt(BUNDLE_KEY_LIMIT, LIMIT_DEFAULT);
        ArrayList<BaseRecyclerData> parcelableArrayList = getArguments().getParcelableArrayList(BUNDLE_KEY_LIST);
        if (parcelableArrayList != null) {
            mAdapter.getList().clear();
            mAdapter.getList().addAll(parcelableArrayList);
        }
        mProvider = provideDataList(mAdapter, new OnInsertData());
        provideDataAndStart();
    }

    @Override
    public void onDestroyView() {
        getArguments().putInt(BUNDLE_KEY_SCROLL_POSITION, mLayoutManager.findFirstVisibleItemPosition());
        getArguments().putInt(BUNDLE_KEY_LIMIT, mLimit);
        getArguments().putLong(BUNDLE_KEY_NEXT_OFFSET, mNextOffset);
        getArguments().putParcelableArrayList(BUNDLE_KEY_LIST, mAdapter.getList());
        super.onDestroyView();
    }

    private void provideDataAndStart() {
        provideData();
        if (mAdapter.isEmpty()) {
            mTryAgainView.start();
        } else {
            mTryAgainView.finish();
            ArrayList<BaseRecyclerData> list = mAdapter.getList();
            if (!list.isEmpty() && list.get(list.size() - 1).getViewType() == TryAgainData.VIEW_TYPE) {
                list.remove(list.size() - 1);
                mAdapter.notifyItemRemoved(list.size() - 1);
            }
        }
    }

    private void provideData() {
        if (mLastTriedOffset == mNextOffset) {
            return;
        }
        mLastTriedOffset = mNextOffset;
        mHandler.post(new Runnable() {
            @Override public void run() {
                mProvider.provideData(mNextOffset, mLimit);
            }
        });
    }

    @Nullable
    protected abstract View getEmptyView();

    protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        return new SparseArrayCompat<Class<? extends BaseViewHolder>>() {{
            put(TryAgainData.VIEW_TYPE, TryAgainViewHolder.class.asSubclass(BaseViewHolder.class));
        }};
    }

    protected abstract P provideDataList(
            RecyclerListAdapter adapter,
            OnInsertData insertData
    );

    protected abstract <T extends BaseViewHolder> Action1<T> getObserver();

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
        if (!mLoading) {
            return;
        }

        int totalItemCount = mLayoutManager.getItemCount();
        int visibleItemCount = mLayoutManager.getChildCount();
        int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
            provideData();
        }
    }

    public class OnInsertData {
        public void onDataInserted(
                boolean isHeader,
                @NonNull DataObserver dataObserver
        ) {
            BaseRecyclerFragment.this.onDataInserted(isHeader, dataObserver);
            BaseRecyclerFragment.this.onNotifyToContinue();
        }

        public void onError(String message) {
            BaseRecyclerFragment.this.onDataInsertedError(message);
        }

        public void onNotifyToContinue() {
            mLoading = true;
            mLastTriedOffset = mNextOffset - mLimit;
            BaseRecyclerFragment.this.onNotifyToContinue();
        }
    }

    private void onDataInserted(
            boolean isHeader,
            @NonNull DataObserver dataObserver
    ) {
        if (mLimit > LIMIT_DEFAULT) {
            mLimit = LIMIT_DEFAULT;
        }
        mRecyclerView.setVisibility(View.VISIBLE);

        final List<BaseRecyclerData> list = mAdapter.getList();
        if (!list.isEmpty() && list.get(list.size() - 1).getViewType() == TryAgainData.VIEW_TYPE) {
            list.remove(list.size() - 1);
        }
        final Deque<BaseRecyclerData> deque = new ArrayDeque<>();

        dataObserver.setDeque(deque);
        Observable.create(new Observable.OnSubscribe<BaseRecyclerData>() {
            @Override public void call(Subscriber<? super BaseRecyclerData> subscriber) {
                for (BaseRecyclerData baseRecyclerData : list) {
                    subscriber.onNext(baseRecyclerData);
                }
                subscriber.onCompleted();
            }
        }).subscribe(dataObserver);
        dataObserver.setDeque(null);
        list.clear();
        list.addAll(deque);

        mLoading = !dataObserver.isEndOfList();

        if (mHeaderSize <= 0 && isHeader) {
            mHeaderSize = list.size();
        }
    }

    private void onDataInsertedError(String message) {
        List<BaseRecyclerData> list = mAdapter.getList();
        if (list.isEmpty()) {
            mTryAgainView.onError(message);
        } else {
            BaseRecyclerData data = list.get(list.size() - 1);
            if (data instanceof TryAgainData) {
                TryAgainData tryAgainData = (TryAgainData) data;
                tryAgainData.setErrorMessage(message);
                mAdapter.notifyItemChanged(list.size() - 1);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onNotifyToContinue() {
        if (mAdapter.getList().size() >= mNextOffset + mLimit + mHeaderSize) {
            mNextOffset += mLimit;
            mLoading = true;
        }

        if (mLoading) {
            mSwipeRefreshLayout.setEnabled(false);
            mAdapter.getList().add(new TryAgainData(true));
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    provideData();
                }
            });
        } else {
            if (hasSwipeRefresh()) {
                mSwipeRefreshLayout.setEnabled(true);
            }
            mAdapter.notifyItemRemoved(mAdapter.getList().size());
        }

        if (!mAdapter.isEmpty()) {
            mTryAgainView.finish();
        } else if (!mLoading) {
            mTryAgainView.showExtraView();
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
    }

    protected boolean hasSwipeRefresh() {
        return true;
    }

    public void setContentObserver(Action1<BaseViewHolder> contentObserver) {
        mContentObserver = contentObserver;
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
