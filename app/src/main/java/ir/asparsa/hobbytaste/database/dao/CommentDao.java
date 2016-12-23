package ir.asparsa.hobbytaste.database.dao;

import ir.asparsa.common.database.model.CommentColumns;
import ir.asparsa.hobbytaste.database.DatabaseHelper;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import rx.Observable;
import rx.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
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
                                            .orderBy(CommentColumns.CREATED, false)
                                            .where()
                                            .eq(CommentColumns.STORE, storeId)
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
                                            .orderBy(CommentColumns.CREATED, false)
                                            .where()
                                            .eq(CommentColumns.STORE, storeId)
                                            .prepare()));

                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Long> countOf(final long id) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override public void call(Subscriber<? super Long> subscriber) {
                try {
                    subscriber.onNext(getDao().countOf(
                            getDao().queryBuilder().where().eq(CommentColumns.ID, id).prepare()));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
