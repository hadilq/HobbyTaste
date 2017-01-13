package ir.asparsa.hobbytaste.core.manager;

import com.j256.ormlite.dao.Dao;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.PageDto;
import ir.asparsa.common.net.dto.StoreCommentDto;
import ir.asparsa.hobbytaste.database.dao.CommentDao;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.net.StoreService;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
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

    @Inject CommentManager() {
    }

    private Observable<PageDto<StoreCommentDto>> getLoadServiceObservable(Constraint constraint) {
        return mStoreService
                .loadComments(
                        constraint.getStore().getId(), (int) (constraint.getOffset() / constraint.getLimit()),
                        constraint.getLimit())
                .retry(5)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());
    }

    private Observable<StoreCommentDto> getSaveServiceObservable(CommentModel comment) {
        return mStoreService
                .saveComment(
                        comment.getStoreId(),
                        new StoreCommentDto(comment.getDescription(), comment.getHashCode()))
                .retry(5)
                .subscribeOn(Schedulers.newThread());
    }

    private Observable<StoreCommentDto> getLikeServiceObservable(
            long storeId,
            long hashCode,
            boolean liked
    ) {
        return mStoreService
                .likeComment(storeId, hashCode, liked)
                .retry(5)
                .subscribeOn(Schedulers.newThread());
    }

    private Observable<List<CommentModel>> getLoadCommentsObservable(Constraint constraint) {
        return mCommentDao
                .queryComments(constraint.getOffset(), (long) constraint.getLimit(),
                               constraint.getStore().getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<CommentModel> getSaveCommentObservable(CommentModel comment) {
        return mCommentDao.createIfNotExists(comment)
                          .subscribeOn(Schedulers.newThread())
                          .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Long> getCountCommentsObservable(StoreModel store) {
        return mCommentDao.countOf(store.getId());
    }

    private Observable<Collection<CommentModel>> getLoadErrorObservable(final Throwable e) {
        return Observable.create(new Observable.OnSubscribe<Collection<CommentModel>>() {
            @Override public void call(Subscriber<? super Collection<CommentModel>> subscriber) {
                subscriber.onError(e);
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<CommentModel> getSaveErrorObservable(final Throwable e) {
        return Observable.create(new Observable.OnSubscribe<CommentModel>() {
            @Override public void call(Subscriber<? super CommentModel> subscriber) {
                subscriber.onError(e);
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<CommentModel> getLikeErrorObservable(final Throwable e) {
        return Observable.create(new Observable.OnSubscribe<CommentModel>() {
            @Override public void call(Subscriber<? super CommentModel> subscriber) {
                subscriber.onError(e);
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    private void requestServer(
            Constraint constraint,
            Observer<Collection<CommentModel>> observer
    ) {
        getLoadServiceObservable(constraint).subscribe(onLoadObserver(constraint, observer));
    }

    private void loadFromDatabase(
            Constraint constraint,
            Observer<Collection<CommentModel>> observer
    ) {
        getLoadCommentsObservable(constraint).subscribe(observer);
    }

    public Subscription saveComment(
            CommentModel comment,
            final Observer<CommentModel> observer
    ) {
        Subject<CommentModel, CommentModel> subject = new SerializedSubject<>(
                PublishSubject.<CommentModel>create());
        Subscription subscription = subject.subscribe(observer);

        getSaveCommentObservable(comment).subscribe(onSaveToDatabaseObserver(subject));

        return subscription;
    }

    public Subscription loadComments(
            final CommentManager.Constraint constraint,
            final Observer<Collection<CommentModel>> observer
    ) {
        Subject<Collection<CommentModel>, Collection<CommentModel>> subject = new SerializedSubject<>(
                PublishSubject.<Collection<CommentModel>>create());
        Subscription subscription = subject.subscribe(observer);

        getCountCommentsObservable(constraint.getStore())
                .subscribe(onCountCommentsObserver(constraint, subject));

        return subscription;
    }

    public Subscription heartBeat(
            CommentModel commentModel,
            Observer<CommentModel> observer
    ) {
        Subject<CommentModel, CommentModel> subject = new SerializedSubject<>(PublishSubject.<CommentModel>create());
        Subscription subscription = subject.subscribe(observer);
        getLikeServiceObservable(commentModel.getStoreId(), commentModel.getHashCode(), commentModel.isLiked())
                .subscribe(onCommentReceivedObserver(commentModel, subject));
        return subscription;
    }

    private Observer<StoreCommentDto> onCommentReceivedObserver(
            final CommentModel oldComment,
            final Observer<CommentModel> observer
    ) {
        return new Observer<StoreCommentDto>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                getLikeErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(StoreCommentDto storeCommentDto) {
                CommentModel newComment = CommentModel.newInstance(storeCommentDto);
                if (!newComment.equals(oldComment)) {
                    getLikeErrorObservable(new RuntimeException(
                            "New comment is different from old comment: " + oldComment + ", " + newComment))
                            .subscribe(observer);
                    return;
                }
                newComment.setId(oldComment.getId());
                mCommentDao.create(newComment)
                           .subscribeOn(AndroidSchedulers.mainThread())
                           .subscribe(onFinishSavingCommentObserver(newComment, observer));
            }
        };
    }

    private Observer<PageDto<StoreCommentDto>> onLoadObserver(
            final Constraint constraint,
            final Observer<Collection<CommentModel>> observer
    ) {
        return new Observer<PageDto<StoreCommentDto>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(PageDto<StoreCommentDto> comments) {
                L.i(CommentManager.class, "Loaded comments from server: " + comments.getList().size() +
                                          " " + comments.getTotalElements());
                List<CommentModel> list = new ArrayList<>();
                for (StoreCommentDto storeCommentDto : comments.getList()) {
                    list.add(CommentModel.newInstance(storeCommentDto));
                }
                mCommentDao.createAll(list)
                           .subscribe(onFinishLoadObserver(constraint, comments.getTotalElements(), observer));
            }
        };
    }

    private Observer<? super Collection<Dao.CreateOrUpdateStatus>> onFinishLoadObserver(
            final Constraint constraint,
            final long totalElements,
            final Observer<Collection<CommentModel>> observer
    ) {
        return new Observer<Collection<Dao.CreateOrUpdateStatus>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.i(CommentManager.class, "Comments cannot finish action", e);
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(Collection<Dao.CreateOrUpdateStatus> statuses) {
                L.i(CommentManager.class, "Comments completely saved");
                loadFromDatabase(constraint, observer);

                mCommentDao.countOf(constraint.getStore().getId())
                           .subscribe(onCheckTotalCountObserver(constraint, totalElements, observer));

            }
        };
    }

    private Observer<Long> onCheckTotalCountObserver(
            final Constraint constraint,
            final long totalElements,
            final Observer<Collection<CommentModel>> observer
    ) {
        return new Observer<Long>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(Long count) {
                L.i(CommentManager.class, "Check for total count: " + count + " " + totalElements);
                if (count < totalElements) {
                    requestServer(new Constraint(constraint.getStore(), constraint.getOffset() + constraint.getLimit(),
                                                 constraint.getLimit()), observer);
                } else if (totalElements < count) {
                    // TODO handle this exception
                }
            }
        };
    }

    private Observer<CommentModel> onSaveToDatabaseObserver(
            final Observer<CommentModel> observer
    ) {
        return new Observer<CommentModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                getSaveErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(CommentModel comment) {
                L.i(CommentManager.class, "Save new comments to database.");
                getSaveServiceObservable(comment)
                        .subscribe(onSaveToServerObserver(comment, observer));
            }
        };
    }

    private Observer<StoreCommentDto> onSaveToServerObserver(
            final CommentModel comment,
            final Observer<CommentModel> observer
    ) {
        return new Observer<StoreCommentDto>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(final Throwable e) {
                mCommentDao.delete(comment)
                           .subscribe(onDeleteOnUnSucceedRequestObserver(e, observer));
            }

            @Override public void onNext(StoreCommentDto storeComment) {
                L.i(CommentManager.class, "Saved on server.");
                CommentModel newComment = CommentModel.newInstance(storeComment);
                if (!comment.equals(newComment)) {
                    mCommentDao.delete(comment)
                               .subscribe(onDeleteOnUnSucceedRequestObserver(
                                       new RuntimeException("Saved comment is not the same as received comment"),
                                       observer));
                    return;
                }
                newComment.setId(comment.getId());
                mCommentDao.create(newComment)
                           .subscribeOn(AndroidSchedulers.mainThread())
                           .subscribe(onFinishSavingCommentObserver(newComment, observer));
            }
        };
    }

    private Observer<? super Dao.CreateOrUpdateStatus> onFinishSavingCommentObserver(
            final CommentModel newComment,
            final Observer<CommentModel> observer
    ) {
        return new Observer<Dao.CreateOrUpdateStatus>() {
            @Override public void onCompleted() {
                // Don't call observer's on completed method
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);

            }

            @Override public void onNext(Dao.CreateOrUpdateStatus createOrUpdateStatus) {
                observer.onNext(newComment);
            }
        };
    }

    private Observer<Integer> onDeleteOnUnSucceedRequestObserver(
            final Throwable e,
            final Observer<CommentModel> observer
    ) {
        return new Observer<Integer>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable ee) {
                ee.initCause(e);
                getSaveErrorObservable(ee).subscribe(observer);
            }

            @Override public void onNext(Integer integer) {
                L.i(CommentManager.class, "Successfully deleted from local database, the unsaved comment on server.");
                getSaveErrorObservable(e).subscribe(observer);
            }
        };
    }

    private Observer<Long> onCountCommentsObserver(
            final Constraint constraint,
            final Observer<Collection<CommentModel>> observer
    ) {
        return new Observer<Long>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.e(CommentManager.class, "Cannot count comments for: " + constraint.getStore(), e);
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(Long count) {
                L.i(
                        CommentManager.class,
                        "Count comments: " + count + ", " + constraint.getOffset() + ", " + constraint.getLimit()
                );
                if (count < constraint.getOffset() + constraint.getLimit()) {
                    requestServer(constraint, observer);
                }

                loadFromDatabase(constraint, observer);
            }
        };
    }

    public static class Constraint {
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
