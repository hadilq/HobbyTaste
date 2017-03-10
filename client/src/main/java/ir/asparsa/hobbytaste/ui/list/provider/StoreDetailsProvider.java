package ir.asparsa.hobbytaste.ui.list.provider;

import android.support.annotation.NonNull;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.data.DataObserver;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.common.net.dto.ErrorDto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.CommentManager;
import ir.asparsa.hobbytaste.core.retrofit.RetrofitException;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.list.data.CommentData;
import ir.asparsa.hobbytaste.ui.list.data.GalleryData;
import ir.asparsa.hobbytaste.ui.list.data.RatingData;
import ir.asparsa.hobbytaste.ui.list.data.StoreMapData;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;
import java.util.*;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreDetailsProvider extends AbsListProvider implements Observer<Collection<CommentModel>> {

    @Inject
    CommentManager mCommentManager;

    private final StoreModel mStore;
    private CompositeSubscription mSubscription = new CompositeSubscription();

    public StoreDetailsProvider(
            @NonNull RecyclerListAdapter adapter,
            @NonNull BaseRecyclerFragment.OnInsertData insertData,
            @NonNull final StoreModel store
    ) {
        super(adapter, insertData);
        ApplicationLauncher.mainComponent().inject(this);
        this.mStore = store;

        mOnInsertData.OnDataInserted(new DataObserver() {
            @Override public void onCompleted() {
                deque.add(new StoreMapData(mStore));
                if (mStore.getBanners() != null && mStore.getBanners().size() != 0) {
                    deque.add(new GalleryData(mStore.getBanners()));
                }
                deque.add(new RatingData(mStore.getRate(), mStore.getViewed(), mStore.getDescription(),
                                         mStore.isLiked()));
            }

            @Override public void onNext(BaseRecyclerData baseRecyclerData) {
            }
        });
    }

    @Override public void provideData(
            long offset,
            int limit
    ) {
        L.i(getClass(), "Provide data: " + offset + " " + limit);
        mSubscription.add(mCommentManager.loadComments(new CommentManager.Constraint(mStore, offset, limit), this));
    }

    @Override public void onCompleted() {

    }

    @Override public void onError(Throwable e) {
        L.e(getClass(), "An error happened!", e);
        ErrorDto error = null;
        if (e instanceof RetrofitException) {
            error = ((RetrofitException) e).getErrorBody();
        }
        if (error == null) {
            mOnInsertData.onError(e.getLocalizedMessage());
        } else {
            mOnInsertData.onError(((RetrofitException) e).getErrorBody().getLocalizedMessage());
        }
    }

    @Override public void onNext(final Collection<CommentModel> collection) {
        L.i(getClass(), "Collection of comments" + collection);
        for (CommentModel commentModel : collection) {
            if (commentModel.getStoreId() != mStore.getId()) {
                return;
            }
        }

        final List<CommentModel> listModel = new ArrayList<>(collection);
        Collections.sort(listModel, new Comparator<CommentModel>() {
            @Override public int compare(
                    CommentModel comment1,
                    CommentModel comment2
            ) {
                return (int) (comment2.getCreated() - comment1.getCreated());
            }
        });

        final DataObserver dataHolder = new DataObserver() {
            @Override public void onCompleted() {
                for (; index < listModel.size(); index++) {
                    deque.add(new CommentData(listModel.get(index)));
                    mAdapter.notifyItemInserted(deque.size() - 1);
                }
            }

            @Override public void onNext(BaseRecyclerData baseRecyclerData) {
                if (!(baseRecyclerData instanceof CommentData)) {
                    deque.add(baseRecyclerData);
                    return;
                }

                CommentModel addedBeforeComment = ((CommentData) baseRecyclerData).getCommentModel();
                boolean missRecyclerData = true;

                for (; index < listModel.size(); index++) {
                    CommentModel comment = listModel.get(index);
                    if (addedBeforeComment.equals(comment)) {
                        missRecyclerData = false;
                        deque.add(baseRecyclerData);
                        index++;
                        break;
                    } else if (comment.getCreated() > addedBeforeComment.getCreated()) {
                        deque.add(new CommentData(comment));
                        mAdapter.notifyItemInserted(deque.size() - 1);
                    } else if (comment.getCreated() < addedBeforeComment.getCreated()) {
                        missRecyclerData = false;
                        deque.add(baseRecyclerData);
                        break;
                    }
                }

                if (missRecyclerData) {
                    deque.add(baseRecyclerData);
                }
            }
        };

        mOnInsertData.OnDataInserted(dataHolder);

    }

    public void addComment(CommentModel comment) {
        provideData(0L, 5);
    }

    public void clear() {
        mSubscription.clear();
    }
}
