package ir.asparsa.hobbytaste.ui.list.provider;

import android.support.annotation.NonNull;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.data.DataObserver;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.CommentManager;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.list.data.CommentData;
import ir.asparsa.hobbytaste.ui.list.data.GalleryData;
import ir.asparsa.hobbytaste.ui.list.data.RatingData;
import ir.asparsa.hobbytaste.ui.list.data.StoreMapData;
import rx.Observer;

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
                deque.add(new RatingData(mStore.getRate()));
            }

            @Override public void onNext(BaseRecyclerData baseRecyclerData) {
            }
        });
    }

    @Override public void provideData(
            long offset,
            int limit
    ) {
        mCommentManager
                .loadComments(new CommentManager.Constraint(mStore, offset, limit), this);
    }

    @Override public void onCompleted() {

    }

    @Override public void onError(Throwable e) {
        L.e(getClass(), "An error happened!", e);
    }

    @Override public void onNext(final Collection<CommentModel> collection) {
        final List<CommentModel> listModel = new ArrayList<>(collection);
        Collections.sort(listModel, new Comparator<CommentModel>() {
            @Override public int compare(
                    CommentModel comment1,
                    CommentModel comment2
            ) {
                return (int) (comment1.getCreated() - comment2.getCreated());
            }
        });

        final DataObserver dataHolder = new DataObserver() {
            @Override public void onCompleted() {
                for (; index < listModel.size(); index++) {
                    deque.add(new CommentData(listModel.get(index)));
                }
            }

            @Override public void onNext(BaseRecyclerData baseRecyclerData) {
                if (!(baseRecyclerData instanceof CommentData)) {
                    deque.add(baseRecyclerData);
                    return;
                }

                CommentModel addedBeforeComment = ((CommentData) baseRecyclerData).getCommentModel();
                boolean missRecyclerData = false;

                for (; index < listModel.size(); index++) {
                    CommentModel comment = listModel.get(index);
                    if (addedBeforeComment.equals(comment)) {
                        deque.add(baseRecyclerData);
                        index++;
                        break;
                    } else if (comment.getCreated() > addedBeforeComment.getCreated()) {
                        deque.add(new CommentData(comment));
                        missRecyclerData = true;
                    } else if (comment.getCreated() < addedBeforeComment.getCreated()) {
                        if (missRecyclerData) {
                            index++;
                            break;
                        }
                        deque.add(baseRecyclerData);
                        deque.add(new CommentData(comment));
                        index++;
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
}
