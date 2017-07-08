package ir.asparsa.hobbytaste.core.manager;

import android.support.annotation.NonNull;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.StoreProto;
import ir.asparsa.hobbytaste.database.dao.BannerDao;
import ir.asparsa.hobbytaste.database.dao.StoreDao;
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
import java.util.ArrayDeque;
import java.util.Collection;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
@Singleton
public class StoresManager {

    @Inject
    StoreService mStoreService;
    @Inject
    StoreDao mStoreDao;
    @Inject
    BannerDao mBannerDao;

    @Inject StoresManager() {
    }

    private Observable<StoreDao.Stores> getStoresObservable(Constraint constraint) {
        return mStoreDao.queryStores(
                constraint.latitude, constraint.longitude, constraint.offset, constraint.limit, mBannerDao);
    }

    private Observable<StoreModel> getSaveStoresObservable(@NonNull final StoreModel newModel) {
        return mStoreDao
                .create(mBannerDao, null, newModel);
    }

    private Observable<StoreProto.Stores> getLoadServiceObservable(Constraint constraint) {
        return mStoreService
                .loadStoreModels(
                        constraint.latitude, constraint.longitude,
                        (int) (constraint.getOffset() / constraint.getLimit()), constraint.getLimit())
                .retry(5);
    }

    private Observable<StoreProto.Store> getSaveServiceObservable(StoreProto.Store store) {
        return mStoreService
                .saveStore(store)
                .retry(5);
    }

    private Observable<StoreProto.Store> getLikeServiceObservable(
            long storeHashCode,
            boolean like
    ) {
        return mStoreService
                .like(storeHashCode, like)
                .retry(5);
    }

    private Observable<StoreProto.Store> getViewServiceObservable(
            long storeHashCode
    ) {
        return mStoreService
                .storeViewed(storeHashCode);
    }

    private void requestServer(
            Constraint constraint,
            Observer<StoresResult> observer
    ) {
        getLoadServiceObservable(constraint)
                .subscribe(onLoadObserver(constraint, observer));
    }

    private void loadFromDatabaseThenRequest(
            Constraint constraint,
            Observer<StoresResult> observer
    ) {
        getStoresObservable(constraint)
                .subscribeOn(Schedulers.newThread())
                .subscribe(getLoadThenRequestObserver(constraint, observer));
    }

    private void loadFromDatabase(
            Constraint constraint,
            Observer<StoresResult> observer
    ) {
        getStoresObservable(constraint)
                .subscribe(getLoadObserver(observer));
    }

    public Subscription loadStores(
            Constraint constraint,
            Observer<StoresResult> observer
    ) {
        Subject<StoresResult, StoresResult> subject = new SerializedSubject<>(
                PublishSubject.<StoresResult>create());
        Subscription subscribe = subject.observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

        loadFromDatabaseThenRequest(constraint, subject);

        return subscribe;
    }


    public Subscription heartBeat(
            StoreModel store,
            Observer<StoreModel> observer
    ) {
        Subject<StoreModel, StoreModel> subject = new SerializedSubject<>(PublishSubject.<StoreModel>create());
        Subscription subscription = subject.observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        getLikeServiceObservable(store.getHashCode(), store.isLiked())
                .subscribeOn(Schedulers.newThread())
                .subscribe(onNewStoreReceivedObserver(store, subject));
        return subscription;
    }

    public Subscription viewed(
            StoreModel store,
            Observer<StoreModel> observer
    ) {
        Subject<StoreModel, StoreModel> subject = new SerializedSubject<>(PublishSubject.<StoreModel>create());
        Subscription subscription = subject.observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        getViewServiceObservable(store.getHashCode())
                .subscribeOn(Schedulers.newThread())
                .subscribe(onNewStoreReceivedObserver(store, subject));
        return subscription;
    }

    public Subscription saveStore(
            StoreModel store,
            Observer<StoreModel> observer
    ) {
        Subject<StoreModel, StoreModel> subject = new SerializedSubject<>(PublishSubject.<StoreModel>create());
        Subscription subscription = subject.observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        getSaveStoresObservable(store)
                .subscribeOn(Schedulers.newThread())
                .subscribe(onSaveStoreObserver(subject));
        return subscription;
    }

    private Observer<? super StoreModel> onSaveStoreObserver(final Observer<StoreModel> observer) {
        return new Observer<StoreModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(StoreModel storeModel) {
                getSaveServiceObservable(storeModel.convertToDto())
                        .subscribe(onSavedStoreReceivedObserver(storeModel, observer));
            }
        };
    }

    private Observer<? super StoreProto.Store> onSavedStoreReceivedObserver(
            final StoreModel oldStore,
            final Observer<StoreModel> observer
    ) {
        return new Observer<StoreProto.Store>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                mStoreDao.delete(mBannerDao, oldStore).subscribe(onDeleteOnUnSucceedRequestObserver(e, observer));
            }

            @Override public void onNext(StoreProto.Store storeDto) {
                StoreModel newStore = StoreModel.instantiate(storeDto);
                mStoreDao.create(mBannerDao, oldStore, newStore)
                         .subscribe(onFinishSavingObserver(observer));
            }
        };
    }

    private Observer<? super Integer> onDeleteOnUnSucceedRequestObserver(
            final Throwable e,
            final Observer<StoreModel> observer
    ) {
        return new Observer<Integer>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable ee) {
                ee.initCause(e);
                observer.onError(ee);
            }

            @Override public void onNext(Integer integer) {
                L.i(StoresManager.class, "Successfully deleted from local database, the unsaved store on server.");
                observer.onError(e);
            }
        };
    }

    private Observer<? super StoreDao.Stores> getLoadThenRequestObserver(
            final Constraint constraint,
            final Observer<StoresResult> observer
    ) {
        return new Observer<StoreDao.Stores>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(StoreDao.Stores stores) {
                observer.onNext(new StoresResult(stores.getStores(), stores.getTotalElements()));

                requestServer(constraint, observer);
            }
        };
    }

    private Observer<? super StoreDao.Stores> getLoadObserver(final Observer<StoresResult> observer) {
        return new Observer<StoreDao.Stores>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(StoreDao.Stores stores) {
                observer.onNext(new StoresResult(stores.getStores(), stores.getTotalElements()));
            }
        };
    }

    private Observer<? super StoreProto.Store> onNewStoreReceivedObserver(
            final StoreModel oldStore,
            final Observer<StoreModel> observer
    ) {
        return new Observer<StoreProto.Store>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(StoreProto.Store storeDto) {
                StoreModel newStore = StoreModel.instantiate(storeDto);
                mStoreDao.create(mBannerDao, oldStore, newStore)
                         .subscribe(onFinishSavingObserver(observer));
            }
        };
    }

    private Observer<? super StoreModel> onFinishSavingObserver(
            final Observer<StoreModel> observer
    ) {
        return new Observer<StoreModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(StoreModel newStore) {
                observer.onNext(newStore);
            }
        };
    }


    private Observer<StoreProto.Stores> onLoadObserver(
            final Constraint constraint,
            final Observer<StoresResult> observer
    ) {
        return new Observer<StoreProto.Stores>() {
            @Override public void onCompleted() {
                L.i(StoresManager.class, "Refresh request gets completed");
            }

            @Override public void onError(Throwable e) {
                L.w(StoresManager.class, "Refresh request gets error", e);
                observer.onError(e);
            }

            @Override public void onNext(StoreProto.Stores stores) {
                L.i(StoresManager.class, "Stores successfully received: " + stores);
                if (stores.getStoreCount() == 0) {
                    return;
                }
                Collection<StoreModel> collection = new ArrayDeque<>();
                for (StoreProto.Store store : stores.getStoreList()) {
                    collection.add(StoreModel.instantiate(store));
                }
                mStoreDao.createAll(mBannerDao, collection)
                         .subscribe(onFinishObserver(constraint, observer));
            }
        };
    }

    private Observer<? super Collection<StoreModel>> onFinishObserver(
            final Constraint constraint,
            final Observer<StoresResult> observer
    ) {
        return new Observer<Collection<StoreModel>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.i(StoresManager.class, "Stores cannot finish action", e);
                observer.onError(e);
            }

            @Override public void onNext(Collection<StoreModel> list) {
                L.i(StoresManager.class, "Stores completely saved");
                loadFromDatabase(constraint, observer);
            }
        };
    }

    public static class Constraint {
        private double latitude;
        private double longitude;
        private int offset;
        private int limit;

        public Constraint(
                double latitude,
                double longitude,
                int offset,
                int limit
        ) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.offset = offset;
            this.limit = limit;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public long getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }
    }

    public static class StoresResult {
        private Collection<StoreModel> stores;
        private long totalElements;

        StoresResult(
                Collection<StoreModel> stores,
                long totalElement
        ) {
            this.stores = stores;
            this.totalElements = totalElement;
        }

        public Collection<StoreModel> getStores() {
            return stores;
        }

        public long getTotalElements() {
            return totalElements;
        }
    }

}
