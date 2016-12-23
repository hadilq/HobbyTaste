package ir.asparsa.hobbytaste.core.manager;

import com.j256.ormlite.dao.Dao;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.PageDto;
import ir.asparsa.common.net.dto.ResponseDto;
import ir.asparsa.common.net.dto.StoreCommentDto;
import ir.asparsa.hobbytaste.database.dao.CommentDao;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.net.StoreService;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by hadi on 12/18/2016 AD.
 */
@Singleton
public class CommentManager {

    @Inject
    StoreService mStoreService;
    @Inject
    CommentDao mCommentDao;

    private final Subject<Collection<CommentModel>, Collection<CommentModel>> mLoadSubject = new SerializedSubject<>(
            PublishSubject.<Collection<CommentModel>>create());

    @Inject
    public CommentManager() {
    }

    public Subscription subscribeLoad(Observer<Collection<CommentModel>> observer) {
        return mLoadSubject.subscribe(observer);
    }

    public Observable<List<CommentModel>> getLoadCommentsObservable(Constraint constraint) {
        return mCommentDao
                .queryComments(constraint.getOffset(), (long) constraint.getLimit(),
                               constraint.getStore().getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PageDto<StoreCommentDto>> getLoadServiceObservable(Constraint constraint) {
        return mStoreService
                .loadComments(
                        constraint.getStore().getId(), (int) (constraint.getOffset() / constraint.getLimit()),
                        constraint.getLimit())
                .retry(5)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());
    }

    public Observer<PageDto<StoreCommentDto>> getLoadObserver(final Constraint constraint) {
        return new Observer<PageDto<StoreCommentDto>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(PageDto<StoreCommentDto> comments) {
                List<CommentModel> list = new ArrayList<>();
                for (StoreCommentDto storeCommentDto : comments.getList()) {
                    list.add(new CommentModel(storeCommentDto.getId(), storeCommentDto.getDescription(),
                                              storeCommentDto.getRate(), storeCommentDto.getCreated(),
                                              storeCommentDto.getStoreId()));
                }
                mCommentDao.createAll(list).subscribe(finishLoadAction(constraint, list));
            }
        };
    }

    public Observer<? super Collection<Dao.CreateOrUpdateStatus>> finishLoadAction(
            final Constraint constraint,
            final List<CommentModel> list
    ) {
        return new Observer<Collection<Dao.CreateOrUpdateStatus>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.i(StoresManager.class, "Comments cannot finish action", e);
            }

            @Override public void onNext(Collection<Dao.CreateOrUpdateStatus> statuses) {
                L.i(StoresManager.class, "Comments completely saved");
                loadFromDb(constraint);

                mCommentDao.queryComments(constraint.getStore().getId()).subscribe(
                        compareComments(constraint, list));
            }
        };
    }

    private Observer<? super List<CommentModel>> compareComments(
            final Constraint constraint,
            final List<CommentModel> receivedList
    ) {
        return new Observer<List<CommentModel>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(List<CommentModel> loadedList) {
                if (loadedList.size() > 0 && receivedList.size() > 0 &&
                    loadedList.get(0).getCreated() < receivedList.get(receivedList.size() - 1).getCreated()) {
                    requestServer(new Constraint(constraint.getStore(), constraint.getOffset() + constraint.getLimit(),
                                                 constraint.getLimit()));
                }
            }
        };
    }

    public Observable<CommentModel> getSaveCommentObservable(CommentModel comment) {
        return mCommentDao.createIfNotExists(comment)
                          .subscribeOn(Schedulers.newThread())
                          .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseDto> getSaveServiceObservable(CommentModel comment) {
        return mStoreService
                .saveComment(
                        comment.getStoreId(),
                        new StoreCommentDto(comment.getDescription(), comment.hashCode()))
                .retry(5)
                .subscribeOn(Schedulers.newThread());
    }

    public void saveComment(
            CommentModel comment,
            final Observer<Void> observer
    ) {
        getSaveCommentObservable(comment).subscribe(saveToDb(observer));
    }

    private Observer<CommentModel> saveToDb(
            final Observer<Void> observer
    ) {
        return new Observer<CommentModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(CommentModel comment) {
                getSaveServiceObservable(comment).subscribe(saveToServer(comment, observer));
            }
        };
    }

    private Observer<ResponseDto> saveToServer(
            final CommentModel comment,
            final Observer<Void> observer
    ) {
        return new Observer<ResponseDto>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(final Throwable e) {
                mCommentDao.delete(comment).subscribe(deleteOnUnSucceedRequest(e, observer));
            }

            @Override public void onNext(ResponseDto responseDto) {
                observer.onNext(null);
            }
        };
    }

    private Observer<Integer> deleteOnUnSucceedRequest(
            final Throwable e,
            final Observer<Void> observer
    ) {
        return new Observer<Integer>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(Integer integer) {
                observer.onError(e);
            }
        };
    }

    public void requestServer(Constraint constraint) {
        getLoadServiceObservable(constraint).subscribe(getLoadObserver(constraint));
    }

    public Observable<Long> countComments(StoreModel store) {
        return mCommentDao.countOf(store.getId());
    }

    public void loadFromDb(Constraint constraint) {
        getLoadCommentsObservable(constraint).subscribe(mLoadSubject);
    }


    public Subscription loadComments(
            final CommentManager.Constraint constraint,
            Observer<Collection<CommentModel>> observer
    ) {
        Subscription subscription = subscribeLoad(observer);
        if (constraint.getOffset() == 0L) {
            requestServer(constraint);
            return subscription;
        }

        countComments(constraint.getStore()).subscribe(
                new Observer<Long>() {
                    @Override public void onCompleted() {
                    }

                    @Override public void onError(Throwable e) {
                        L.e(CommentManager.class, "Cannot count comments for: " + constraint.getStore(), e);
                    }

                    @Override public void onNext(Long count) {
                        if (count < constraint.getOffset() + constraint.getLimit()) {
                            requestServer(constraint);
                            return;
                        }

                        loadFromDb(constraint);
                    }
                }
        );
        return subscription;
    }

    public static class Constraint implements RefreshManager.Constraint {
        private StoreModel store;
        private long offset;
        private int limit;

        public Constraint(
                StoreModel store,
                long offset,
                int limit
        ) {
            this.store = store;
            this.offset = offset;
            this.limit = limit;
        }

        public StoreModel getStore() {
            return store;
        }

        public long getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }
    }
}
