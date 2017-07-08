package ir.asparsa.hobbytaste.core.manager;

import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.CommentProto;
import ir.asparsa.hobbytaste.database.dao.CommentDao;
import ir.asparsa.hobbytaste.database.dao.StoreDao;
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
 * @author hadi
 * @since 12/18/2016 AD.
 */
@Singleton
public class CommentManager {

    @Inject
    StoreService mStoreService;
    @Inject
    CommentDao mCommentDao;
    @Inject
    StoreDao mStoreDao;

    @Inject CommentManager() {
    }

    private Observable<CommentProto.Comments> getLoadServiceObservable(Constraint constraint) {
        return mStoreService
                .loadComments(
                        constraint.getStore().getHashCode(), (int) (constraint.getOffset() / constraint.getLimit()),
                        constraint.getLimit())
                .retry(5);
    }

    private Observable<CommentProto.Comment> getSaveServiceObservable(
            StoreModel store,
            CommentModel comment
    ) {
        return mStoreService
                .saveComment(
                        store.getHashCode(),
                        comment.convertToDto())
                .retry(5);
    }

    private Observable<CommentProto.Comment> getLikeServiceObservable(
            long storeHashCode,
            long commentHashCode,
            boolean liked
    ) {
        return mStoreService
                .likeComment(storeHashCode, commentHashCode, liked)
                .retry(5);
    }

    private Observable<CommentDao.Comments> getLoadCommentsObservable(Constraint constraint) {
        return mCommentDao
                .queryComments(constraint.getOffset(), (long) constraint.getLimit(),
                               constraint.getStore().getId());
    }

    private Observable<CommentModel> getSaveCommentObservable(CommentModel comment) {
        return mCommentDao.createIfNotExists(comment);
    }

    private void requestServer(
            Constraint constraint,
            Observer<CommentsResult> observer
    ) {
        getLoadServiceObservable(constraint).subscribe(onLoadObserver(constraint, observer));
    }

    private void loadFromDatabaseThenRequest(
            Constraint constraint,
            final Observer<CommentsResult> observer
    ) {
        getLoadCommentsObservable(constraint)
                .subscribeOn(Schedulers.newThread())
                .subscribe(onLoadFromDatabaseThenRequestObserver(constraint, observer));
    }

    private void loadFromDatabase(
            Constraint constraint,
            final Observer<CommentsResult> observer
    ) {
        getLoadCommentsObservable(constraint)
                .subscribe(onLoadFromDatabaseObserver(observer));
    }

    public Subscription saveComment(
            StoreModel store,
            CommentModel comment,
            final Observer<CommentModel> observer
    ) {
        Subject<CommentModel, CommentModel> subject = new SerializedSubject<>(
                PublishSubject.<CommentModel>create());
        Subscription subscription = subject.observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

        getSaveCommentObservable(comment)
                .subscribeOn(Schedulers.newThread())
                .subscribe(onSaveToDatabaseObserver(store, subject));

        return subscription;
    }

    public Subscription loadComments(
            final CommentManager.Constraint constraint,
            final Observer<CommentsResult> observer
    ) {
        Subject<CommentsResult, CommentsResult> subject = new SerializedSubject<>(
                PublishSubject.<CommentsResult>create());
        Subscription subscription = subject.observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

        loadFromDatabaseThenRequest(constraint, subject);

        return subscription;
    }

    public Subscription heartBeat(
            StoreModel store,
            CommentModel commentModel,
            Observer<CommentModel> observer
    ) {
        Subject<CommentModel, CommentModel> subject = new SerializedSubject<>(PublishSubject.<CommentModel>create());
        Subscription subscription = subject.observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

        getLikeServiceObservable(store.getHashCode(), commentModel.getHashCode(), commentModel.isLiked())
                .subscribeOn(Schedulers.newThread())
                .subscribe(onCommentReceivedObserver(store, commentModel, subject));
        return subscription;
    }

    private Observer<CommentProto.Comment> onCommentReceivedObserver(
            final StoreModel store,
            final CommentModel oldComment,
            final Observer<CommentModel> observer
    ) {
        return new Observer<CommentProto.Comment>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(CommentProto.Comment storeCommentDto) {
                if (store.getHashCode() != storeCommentDto.getStoreHashCode()) {
                    observer.onError(new RuntimeException("Returned store from server is wrong"));
                    return;
                }
                CommentModel newComment = CommentModel.newInstance(store.getId(), storeCommentDto);
                if (!newComment.equals(oldComment)) {
                    observer.onError(new RuntimeException(
                            "New comment is different from old comment: " + oldComment + ", " + newComment));
                    return;
                }
                newComment.setId(oldComment.getId());
                mCommentDao.create(newComment)
                           .subscribe(onFinishSavingCommentObserver(newComment, observer));
            }
        };
    }

    private Observer<CommentProto.Comments> onLoadObserver(
            final Constraint constraint,
            final Observer<CommentsResult> observer
    ) {
        return new Observer<CommentProto.Comments>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(CommentProto.Comments comments) {
                L.i(
                        CommentManager.class,
                        "Loaded comments from server for these constraints: " + constraint.getOffset() + ", " +
                        constraint.getLimit() + ":\n" + comments);
                List<CommentModel> list = new ArrayList<>();
                for (CommentProto.Comment storeCommentDto : comments.getCommentList()) {
                    if (constraint.getStore().getHashCode() != storeCommentDto.getStoreHashCode()) {
                        observer.onError(new RuntimeException("Returned store from server is wrong"));
                        return;
                    }
                    list.add(CommentModel.newInstance(constraint.getStore().getId(), storeCommentDto));
                }
                mCommentDao.createAll(list)
                           .subscribe(onFinishLoadObserver(constraint, observer));
            }
        };
    }

    private Observer<? super Collection<CommentModel>> onFinishLoadObserver(
            final Constraint constraint,
            final Observer<CommentsResult> observer
    ) {
        return new Observer<Collection<CommentModel>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.i(CommentManager.class, "Comments cannot finish action", e);
                observer.onError(e);
            }

            @Override public void onNext(Collection<CommentModel> comments) {
                L.i(CommentManager.class, "Comments completely saved");
                loadFromDatabase(constraint, observer);
            }
        };
    }

    private Observer<CommentDao.Comments> onLoadFromDatabaseThenRequestObserver(
            final Constraint constraint,
            final Observer<CommentsResult> observer
    ) {
        return new Observer<CommentDao.Comments>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(CommentDao.Comments comments) {
                observer.onNext(new CommentsResult(comments.getComments(), comments.getTotalElements()));

                requestServer(constraint, observer);
            }
        };
    }

    private Observer<CommentDao.Comments> onLoadFromDatabaseObserver(
            final Observer<CommentsResult> observer
    ) {
        return new Observer<CommentDao.Comments>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(CommentDao.Comments comments) {
                observer.onNext(new CommentsResult(comments.getComments(), comments.getTotalElements()));
            }
        };
    }

    private Observer<CommentModel> onSaveToDatabaseObserver(
            final StoreModel store,
            final Observer<CommentModel> observer
    ) {
        return new Observer<CommentModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(CommentModel comment) {
                L.i(CommentManager.class, "Save new comments to database.");
                getSaveServiceObservable(store, comment)
                        .subscribe(onSaveToServerObserver(store, comment, observer));
            }
        };
    }

    private Observer<CommentProto.Comment> onSaveToServerObserver(
            final StoreModel store,
            final CommentModel comment,
            final Observer<CommentModel> observer
    ) {
        return new Observer<CommentProto.Comment>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(final Throwable e) {
                mCommentDao.delete(comment)
                           .subscribe(onDeleteOnUnSucceedRequestObserver(e, observer));
            }

            @Override public void onNext(CommentProto.Comment storeComment) {
                L.i(CommentManager.class, "Saved on server.");
                if (store.getHashCode() != storeComment.getStoreHashCode()) {
                    mCommentDao.delete(comment)
                               .subscribe(onDeleteOnUnSucceedRequestObserver(
                                       new RuntimeException("Returned store from server is wrong"), observer));
                    return;
                }
                CommentModel newComment = CommentModel.newInstance(store.getId(), storeComment);
                if (!comment.equals(newComment)) {
                    mCommentDao.delete(comment)
                               .subscribe(onDeleteOnUnSucceedRequestObserver(
                                       new RuntimeException("Saved comment is not the same as received comment"),
                                       observer));
                    return;
                }
                newComment.setId(comment.getId());
                mCommentDao.create(newComment)
                           .subscribe(onFinishSavingCommentObserver(newComment, observer));
            }
        };
    }

    private Observer<? super Boolean> onFinishSavingCommentObserver(
            final CommentModel newComment,
            final Observer<CommentModel> observer
    ) {
        return new Observer<Boolean>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);

            }

            @Override public void onNext(Boolean createOrUpdateStatus) {
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
                observer.onError(ee);
            }

            @Override public void onNext(Integer integer) {
                L.i(CommentManager.class, "Successfully deleted from local database, the unsaved comment on server.");
                observer.onError(e);
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

    public static class CommentsResult {
        private Collection<CommentModel> comments;
        private long totalElements;

        public CommentsResult(
                Collection<CommentModel> comments,
                long totalElement
        ) {
            this.comments = comments;
            this.totalElements = totalElement;
        }

        public Collection<CommentModel> getComments() {
            return comments;
        }

        public long getTotalElements() {
            return totalElements;
        }
    }
}
