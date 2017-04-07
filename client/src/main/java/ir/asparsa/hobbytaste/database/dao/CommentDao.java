package ir.asparsa.hobbytaste.database.dao;

import com.j256.ormlite.dao.Dao;
import ir.asparsa.common.database.model.Comment;
import ir.asparsa.hobbytaste.database.DatabaseHelper;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import rx.Observable;
import rx.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Singleton
public class CommentDao extends AbsDao<CommentModel, Integer> {

    @Inject
    public CommentDao(DatabaseHelper mDatabaseHelper) {
        super(mDatabaseHelper);
    }

    @Override protected Class<CommentModel> getModel() {
        return CommentModel.class;
    }

    public Observable<List<CommentModel>> queryComments(
            final long offset,
            final long limit,
            final long storeId
    ) {
        return Observable.create(new Observable.OnSubscribe<List<CommentModel>>() {
            @Override public void call(final Subscriber<? super List<CommentModel>> subscriber) {
                try {
                    subscriber.onNext(
                            getDao().query(
                                    getDao().queryBuilder()
                                            .offset(offset)
                                            .limit(limit)
                                            .orderBy(Comment.Columns.CREATED, false)
                                            .where()
                                            .eq(Comment.Columns.STORE, storeId)
                                            .prepare()));

                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<List<CommentModel>> queryComments(
            final long storeId
    ) {
        return Observable.create(new Observable.OnSubscribe<List<CommentModel>>() {
            @Override public void call(final Subscriber<? super List<CommentModel>> subscriber) {
                try {
                    subscriber.onNext(
                            getDao().query(
                                    getDao().queryBuilder()
                                            .orderBy(Comment.Columns.CREATED, false)
                                            .where()
                                            .eq(Comment.Columns.STORE, storeId)
                                            .prepare()));

                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Long> countOf(final long storeId) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override public void call(Subscriber<? super Long> subscriber) {
                try {
                    subscriber.onNext(getDao().countOf(
                            getDao().queryBuilder()
                                    .setCountOf(true)
                                    .where()
                                    .eq(Comment.Columns.STORE, storeId)
                                    .prepare()));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override public Observable<Collection<CommentModel>> createAll(final Collection<CommentModel> data) {
        return Observable.create(new Observable.OnSubscribe<Collection<CommentModel>>() {
            @Override public void call(Subscriber<? super Collection<CommentModel>> subscriber) {
                try {
                    boolean exist;
                    Collection<CommentModel> collection = new ArrayDeque<>();
                    List<CommentModel> comments = getDao().queryForAll();
                    for (CommentModel newComment : data) {
                        exist = false;
                        for (CommentModel commentModel : comments) {
                            if (newComment.equals(commentModel)) {
                                newComment.setId(commentModel.getId());
                                getDao().update(newComment);
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {
                            newComment.setId(null);
                            collection.add(getDao().createIfNotExists(newComment));
                        }
                    }
                    subscriber.onNext(collection);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
