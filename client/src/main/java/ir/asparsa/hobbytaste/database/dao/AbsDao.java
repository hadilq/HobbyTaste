package ir.asparsa.hobbytaste.database.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.hobbytaste.database.DatabaseHelper;
import junit.framework.Assert;
import rx.Observable;
import rx.Subscriber;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
public abstract class AbsDao<T extends BaseModel<ID>, ID> {

    DatabaseHelper mDatabaseHelper;
    public Dao<T, ID> dao;

    AbsDao(DatabaseHelper mDatabaseHelper) {
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

    public Observable<Boolean> create(final T data) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    subscriber.onNext(createOrUpdate(data));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * Due to not working OrmLite implementation of createOrUpdate method
     * */
    boolean createOrUpdate(T data) throws SQLException {
        T found = null;
        if (data.getId() != null) {
            found = getDao().queryForId(data.getId());
//            L.d(AbsDao.class, "found: " + found);
        }
        boolean created;
        if (found == null) {
            getDao().create(data);
            created = true;
        } else {
            getDao().update(data);
            created = false;
        }
        Assert.assertNotNull(data.getId());
        return created;
    }

    public Observable<T> createIfNotExists(final T data) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override public void call(Subscriber<? super T> subscriber) {
                try {
                    T ifNotExists = getDao().createIfNotExists(data);
                    Assert.assertNotNull(ifNotExists.getId());
                    subscriber.onNext(ifNotExists);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Collection<T>> createAll(final Collection<T> data) {
        return Observable.create(new Observable.OnSubscribe<Collection<T>>() {
            @Override public void call(Subscriber<? super Collection<T>> subscriber) {
                try {
                    for (T t : data) {
                        createOrUpdate(t);
                    }
                    subscriber.onNext(data);
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

    public Observable<List<T>> query(
            final long limit,
            final long offset
    ) {
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
            final long limit,
            final long offset,
            final String orderByColumn,
            final boolean ascending
    ) {

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

    public Observable<List<T>> query(
            final String where,
            final String column
    ) {
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
