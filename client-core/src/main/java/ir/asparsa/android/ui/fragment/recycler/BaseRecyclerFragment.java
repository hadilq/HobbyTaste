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
import ir.asparsa.android.ui.list.data.DataObserver;
import ir.asparsa.android.ui.list.data.TryAgainData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.holder.TryAgainViewHolder;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.android.ui.view.TryAgainView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

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
    private static final String BUNDLE_KEY_LOADING = "BUNDLE_KEY_LOADING";
    private static final int LIMIT_DEFAULT = 20;

    protected RecyclerListAdapter mAdapter;
    protected P mProvider;
    private LinearLayoutManager mLayoutManager;
    private TryAgainView.OnTryAgainListener mOnTryAgainListener;

    private final Handler mHandler = new Handler();
    private int mLimit = LIMIT_DEFAULT;
    private long mNextOffset = 0;
    private boolean mLoading = true;
    private boolean mTriedInThisInterval = false;
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
                mRecyclerView, mLayoutManager, savedInstanceState, getViewHoldersList(), getObserver());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mProvider = provideDataList(mAdapter, new OnInsertData());
        return rootView;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNextOffset = getArguments().getLong(BUNDLE_KEY_NEXT_OFFSET, 0);
        mScrollPosition = getArguments().getInt(BUNDLE_KEY_SCROLL_POSITION, 0);
        mLimit = getArguments().getInt(BUNDLE_KEY_LIMIT, LIMIT_DEFAULT);
        mLoading = getArguments().getBoolean(BUNDLE_KEY_LOADING);
        provideDataAndStart();
    }

    @Override
    public void onDestroyView() {
        getArguments().putInt(BUNDLE_KEY_SCROLL_POSITION, mLayoutManager.findFirstVisibleItemPosition());
        getArguments().putInt(BUNDLE_KEY_LIMIT, mLimit);
        getArguments().putLong(BUNDLE_KEY_NEXT_OFFSET, mNextOffset);
        getArguments().putBoolean(BUNDLE_KEY_LOADING, mLoading);
        super.onDestroyView();
    }

    private void provideDataAndStart() {
        provideData();
        if (mAdapter.isEmpty()) {
            mTryAgainView.start();
        }
    }

    private void provideData() {
        mProvider.provideData(mNextOffset, mLimit);
        mTriedInThisInterval = true;
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

    protected abstract <T extends Event> Observer<T> getObserver();

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
            final List<BaseRecyclerData> clonedList = new ArrayList<>(list);
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

            boolean endOfList = true;
            if (clonedList.size() == list.size()) {
                for (BaseRecyclerData data : clonedList) {
                    int position = clonedList.indexOf(data);
                    if (!data.equals(list.get(position))) {
                        endOfList = false;
                        break;
                    }
                }
            } else {
                endOfList = false;
            }

            mLoading = !endOfList || !mTriedInThisInterval;

            if (list.size() >= mNextOffset + mLimit) {
                mNextOffset += mLimit;
                mTriedInThisInterval = false;
                mLoading = true;
            }

            if (mLoading) {
                list.add(new TryAgainData(true, mOnTryAgainListener));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkIfReadyToProvide();
                    }
                });
            } else {
                mAdapter.notifyItemRemoved(list.size());
            }

            if (mAdapter.isEmpty() && !mLoading) {
                mTryAgainView.showExtraView();
            }

            if (!mAdapter.isEmpty()) {
                mTryAgainView.finish();
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

    public interface Event {
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
