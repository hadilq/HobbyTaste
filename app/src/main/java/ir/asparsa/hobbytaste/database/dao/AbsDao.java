package ir.asparsa.hobbytaste.database.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import ir.asparsa.hobbytaste.core.model.BaseModel;
import ir.asparsa.hobbytaste.database.DatabaseHelper;
import rx.Observable;
import rx.Subscriber;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
public abstract class AbsDao<T extends BaseModel, ID> {

    DatabaseHelper mDatabaseHelper;
    public Dao<T, ID> dao;

    public AbsDao(DatabaseHelper mDatabaseHelper) {
        this.mDatabaseHelper = mDatabaseHelper;
    }


    public Dao<T, ID> getDao() throws SQLException {
        if (dao == null) {
            return dao = mDatabaseHelper.getDao(getModel());
        }
        return dao;
    }

    protected abstract Class<T> getModel();

    public Observable<List<T>> queryForAll() {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override public void call(Subscriber<? super List<T>> subscriber) {
                try {
                    subscriber.onNext(getDao().queryForAll());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Dao.CreateOrUpdateStatus> create(final T data) {
        return Observable.create(new Observable.OnSubscribe<Dao.CreateOrUpdateStatus>() {
            @Override public void call(Subscriber<? super Dao.CreateOrUpdateStatus> subscriber) {
                try {
                    subscriber.onNext(getDao().createOrUpdate(data));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Collection<Dao.CreateOrUpdateStatus>> createAll(final Collection<T> data) {
        return Observable.create(new Observable.OnSubscribe<Collection<Dao.CreateOrUpdateStatus>>() {
            @Override public void call(Subscriber<? super Collection<Dao.CreateOrUpdateStatus>> subscriber) {
                try {
                    Collection<Dao.CreateOrUpdateStatus> collection = new ArrayDeque<>();
                    for (T t : data) {
                        collection.add(getDao().createOrUpdate(t));
                    }
                    subscriber.onNext(collection);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Integer> delete(final T data) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                try {
                    subscriber.onNext(getDao().delete(data));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Integer> deleteAll(final Collection<T> data) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                try {
                    int result = 0;
                    for (T t : data) {
                        result += getDao().delete(data);
                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<List<T>> query(final PreparedQuery<T> preparedQuery) {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override public void call(Subscriber<? super List<T>> subscriber) {
                try {
                    subscriber.onNext(getDao().query(preparedQuery));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<List<T>> query(final long limit, final long offset) {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override public void call(Subscriber<? super List<T>> subscriber) {
                try {
                    subscriber.onNext(getDao().query(getDao().queryBuilder().offset(offset).limit(limit).prepare()));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<List<T>> query(
            final long limit, final long offset, final String orderByColumn, final boolean ascending) {

        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override public void call(Subscriber<? super List<T>> subscriber) {
                try {
                    List<T> list = getDao().query(
                            getDao().queryBuilder()
                                    .offset(offset)
                                    .limit(limit)
                                    .orderBy(orderByColumn, ascending)
                                    .prepare());
                    subscriber.onNext(list);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<List<T>> query(final String where, final String column) {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override public void call(Subscriber<? super List<T>> subscriber) {
                try {
                    subscriber.onNext(getDao().query(getDao().queryBuilder().where().eq(column, where).prepare()));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Integer> update(final T data) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                try {
                    subscriber.onNext(getDao().update(data));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<T> queryForSameId(final T data) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(getDao().queryForSameId(data));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
