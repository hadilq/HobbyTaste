package ir.asparsa.hobbytaste.ui.fragment.recycler;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.widget.Toast;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.holder.BaseViewHolderFactory;
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
import ir.asparsa.hobbytaste.ui.list.holder.*;
import ir.asparsa.hobbytaste.ui.list.provider.StoreDetailsProvider;
import junit.framework.Assert;
import rx.Observer;
import rx.functions.Action1;
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
    @Inject
    ViewHolderFactory mViewHolderFactory;

    private CompositeSubscription mSubscription = new CompositeSubscription();

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

    @Override protected <T extends BaseViewHolder> Action1<T> getObserver() {
        return new Action1<T>() {
            @Override public void call(T t) {
                if (t instanceof RatingViewHolder) {
                    ((RatingViewHolder) t).clickDashboardStream().subscribe(getOnArrowClickObserver());
                    ((RatingViewHolder) t).clickHeartStream().subscribe(getOnStoreHeartClickObserver());
                } else if (t instanceof CommentViewHolder) {
                    ((CommentViewHolder) t).clickStream().subscribe(getOnCommentHeartClickObserver());
                } else if (t instanceof GalleryViewHolder) {
                    ((GalleryViewHolder) t).clickStream().subscribe(getOnScreenshotClickObserver());
                }
            }
        };
    }

    @Override protected BaseViewHolderFactory getViewHolderFactory() {
        return mViewHolderFactory;
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

    private Action1<RatingData> getOnArrowClickObserver() {
        return new Action1<RatingData>() {
            @Override public void call(RatingData data) {
                data.setShowDescription(!data.isShowDescription());
                int index = mAdapter.getList().indexOf(data);
                if (index != -1) {
                    mAdapter.notifyItemChanged(index);
                }
            }
        };
    }

    private Action1<? super RatingData> getOnStoreHeartClickObserver() {
        return new Action1<RatingData>() {
            @Override public void call(RatingData data) {
                StoreModel store = getStoreModel();
                store.heartBeat();
                data.setLike(store.isLiked());
                data.setRate(store.getRate());
                notifyStoreHeartBeat();
                mSubscription.add(mStoresManager.heartBeat(store, getStoreHeartBeatObserver(data)));
            }
        };
    }

    private Action1<? super CommentData> getOnCommentHeartClickObserver() {
        return new Action1<CommentData>() {
            @Override public void call(CommentData comment) {
                comment.heartBeat();
                notifyCommentHeartBeat(comment);
                StoreModel store = getArguments().getParcelable(BUNDLE_KEY_STORE);
                if (store == null) {
                    L.e(getClass(), "Store is null!");
                    return;
                }
                mSubscription.add(mCommentManager.heartBeat(store, comment.getCommentModel(),
                                                            getCommentHeartBeatObserver(comment)));
            }
        };
    }

    private Action1<? super BannerModel> getOnScreenshotClickObserver() {
        return new Action1<BannerModel>() {
            @Override public void call(BannerModel data) {
                Intent intent = new Intent(getContext(), ScreenshotActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ScreenshotActivity.BUNDLE_KEY_MAIN_URL, data.getMainUrl());
                intent.putExtra(ScreenshotActivity.BUNDLE_KEY_THUMBNAIL_URL, data.getThumbnailUrl());
                getContext().startActivity(intent);
            }
        };
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
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                notifyCommentHeartBeat(comment);
            }

            @Override public void onNext(CommentModel commentModel) {
                comment.setCommentModel(commentModel);
                notifyCommentHeartBeat(comment);
            }
        };
    }

    private void notifyStoreHeartBeat() {
        List<BaseRecyclerData> list = mAdapter.findData(RatingData.class);
        for (BaseRecyclerData data : list) {
            int index = mAdapter.getList().indexOf(data);
            if (index != -1) {
                mAdapter.notifyItemChanged(index);
            }
        }
    }

    private void notifyCommentHeartBeat(CommentData comment) {
        int index = mAdapter.getList().indexOf(comment);
        if (index != -1) {
            mAdapter.notifyItemChanged(index);
        }
    }

    public void addComment(CommentModel comment) {
        mProvider.addComment(comment);
    }
}
