package ir.asparsa.hobbytaste.ui.fragment.recycler;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.CommentManager;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.activity.ScreenshotActivity;
import ir.asparsa.hobbytaste.ui.list.data.CommentData;
import ir.asparsa.hobbytaste.ui.list.data.GalleryData;
import ir.asparsa.hobbytaste.ui.list.data.RatingData;
import ir.asparsa.hobbytaste.ui.list.data.StoreMapData;
import ir.asparsa.hobbytaste.ui.list.holder.CommentViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.GalleryViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.RatingViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.StoreMapViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.StoreDetailsProvider;
import junit.framework.Assert;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;
import java.util.List;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreDetailsRecyclerFragment extends BaseRecyclerFragment<StoreDetailsProvider> {

    public static final String BUNDLE_KEY_STORE = "BUNDLE_KEY_STORE";

    @Inject
    StoresManager mStoresManager;
    @Inject
    CommentManager mCommentManager;

    CompositeSubscription mSubscription = new CompositeSubscription();

    public static StoreDetailsRecyclerFragment instantiate(Bundle bundle) {
        StoreDetailsRecyclerFragment fragment = new StoreDetailsRecyclerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);

        mSubscription.add(mStoresManager.viewed(getStoreModel(), getViewedObserver()));
    }

    @Override
    protected View getEmptyView() {
        return null;
    }

    @Override
    protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        SparseArrayCompat<Class<? extends BaseViewHolder>> array = super.getViewHoldersList();
        array.put(StoreMapData.VIEW_TYPE, StoreMapViewHolder.class.asSubclass(BaseViewHolder.class));
        array.put(GalleryData.VIEW_TYPE, GalleryViewHolder.class.asSubclass(BaseViewHolder.class));
        array.put(RatingData.VIEW_TYPE, RatingViewHolder.class.asSubclass(BaseViewHolder.class));
        array.put(CommentData.VIEW_TYPE, CommentViewHolder.class.asSubclass(BaseViewHolder.class));
        return array;
    }

    @Override
    protected StoreDetailsProvider provideDataList(
            RecyclerListAdapter adapter,
            OnInsertData insertData
    ) {
        return new StoreDetailsProvider(adapter, insertData, getStoreModel());
    }

    @Override protected <T extends Event> Observer<T> getObserver() {
        return new Observer<T>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(T t) {
                if (t instanceof RatingViewHolder.OnHeartClick) {
                    onStoreHeartClick(((RatingViewHolder.OnHeartClick) t).getData());
                } else if (t instanceof CommentViewHolder.OnHeartClick) {
                    onCommentHeartClick(((CommentViewHolder.OnHeartClick) t).getComment());
                } else if (t instanceof GalleryViewHolder.OnScreenshotClick) {
                    GalleryViewHolder.OnScreenshotClick onScreenshotClick = (GalleryViewHolder.OnScreenshotClick) t;
                    onScreenshotClick(onScreenshotClick.getData(), onScreenshotClick.getDrawable());
                }
            }
        };
    }

    @Override public void onDestroyView() {
        mProvider.clear();
        mSubscription.clear();
        super.onDestroyView();
    }

    private StoreModel getStoreModel() {
        StoreModel store = getArguments().getParcelable(BUNDLE_KEY_STORE);
        if (store == null) {
            store = new StoreModel();
            Assert.fail("Store cannot be null!");
        }
        return store;
    }

    private void onStoreHeartClick(RatingData data) {
        StoreModel store = getStoreModel();
        store.heartBeat();
        data.setLike(store.isLiked());
        data.setRate(store.getRate());
        notifyStoreHeartBeat();
        mSubscription.add(mStoresManager.heartBeat(store, getStoreHeartBeatObserver(data)));
    }

    private void onCommentHeartClick(CommentData comment) {
        comment.heartBeat();
        notifyCommentHeartBeat(comment);
        mSubscription.add(mCommentManager.heartBeat(comment.getCommentModel(), getCommentHeartBeatObserver(comment)));
    }

    private void onScreenshotClick(
            BannerModel data,
            Drawable drawable
    ) {
        Intent intent = new Intent(getContext(), ScreenshotActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ScreenshotActivity.BUNDLE_KEY_MAIN_URL, data.getMainUrl());
        intent.putExtra(ScreenshotActivity.BUNDLE_KEY_THUMBNAIL_URL, data.getThumbnailUrl());
        if (drawable instanceof BitmapDrawable) {
            intent.putExtra(ScreenshotActivity.BUNDLE_KEY_IMAGE, ((BitmapDrawable) drawable).getBitmap());
        }
        getContext().startActivity(intent);
    }

    private Observer<StoreModel> getStoreHeartBeatObserver(final RatingData data) {
        return new Observer<StoreModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                StoreModel store = getStoreModel();
                store.heartBeat();
                data.setLike(store.isLiked());
                data.setRate(store.getRate());
                notifyStoreHeartBeat();
            }

            @Override public void onNext(StoreModel storeModel) {
                getArguments().putParcelable(BUNDLE_KEY_STORE, storeModel);
                notifyStoreHeartBeat();
            }
        };
    }

    private Observer<StoreModel> getViewedObserver() {
        return new Observer<StoreModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(StoreModel storeModel) {
                getArguments().putParcelable(BUNDLE_KEY_STORE, storeModel);
            }
        };
    }

    private Observer<CommentModel> getCommentHeartBeatObserver(final CommentData comment) {
        return new Observer<CommentModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                comment.heartBeat();
                notifyCommentHeartBeat(comment);
            }

            @Override public void onNext(CommentModel commentModel) {
                comment.setCommentModel(commentModel);
                notifyCommentHeartBeat(comment);
            }
        };
    }

    private void notifyStoreHeartBeat() {
        List<Integer> list = mAdapter.findViewHolder(RatingViewHolder.class);
        for (Integer integer : list) {
            mAdapter.notifyItemChanged(integer);
        }
    }

    private void notifyCommentHeartBeat(CommentData comment) {
        int index = findViewHolder(comment);
        if (index != -1) {
            mAdapter.notifyItemChanged(index);
        }
    }

    private int findViewHolder(CommentData comment) {
        List<BaseRecyclerData> list = mAdapter.getDataList();
        for (BaseRecyclerData data : list) {
            if (data.equals(comment)) {
                return list.indexOf(data);
            }
        }
        return -1;
    }

    public void addComment(CommentModel comment) {
        mProvider.addComment(comment);
    }

}
