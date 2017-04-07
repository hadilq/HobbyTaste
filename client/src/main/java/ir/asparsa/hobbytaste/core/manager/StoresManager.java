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
import rx.Subscriber;
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
import java.util.List;

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

    private Observable<List<StoreModel>> getStoresObservable() {
        return mStoreDao
                .queryAllStores(mBannerDao)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<StoreModel> getSaveStoresObservable(@NonNull final StoreModel newModel) {
        return mStoreDao
                .create(mBannerDao, null, newModel)
                .subscribeOn(Schedulers.newThread());
    }

    private Observable<StoreProto.Stores> getLoadServiceObservable() {
        return mStoreService
                .loadStoreModels()
                .retry(5)
                .subscribeOn(Schedulers.newThread());
    }

    private Observable<StoreProto.Store> getSaveServiceObservable(StoreProto.Store store) {
        return mStoreService
                .saveStore(store)
                .retry(5)
                .subscribeOn(Schedulers.newThread());
    }

    private Observable<StoreProto.Store> getLikeServiceObservable(
            long storeHashCode,
            boolean like
    ) {
        return mStoreService
                .like(storeHashCode, like)
                .retry(5)
                .subscribeOn(Schedulers.newThread());
    }

    private Observable<StoreProto.Store> getViewServiceObservable(
            long storeHashCode
    ) {
        return mStoreService
                .storeViewed(storeHashCode)
                .subscribeOn(Schedulers.newThread());
    }

    private Observable<Collection<StoreModel>> getLoadErrorObservable(final Throwable e) {
        return Observable.create(new Observable.OnSubscribe<Collection<StoreModel>>() {
            @Override public void call(Subscriber<? super Collection<StoreModel>> subscriber) {
                subscriber.onError(e);
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }


    private Observable<StoreModel> getSaveErrorObservable(final Throwable e) {
        return Observable.create(new Observable.OnSubscribe<StoreModel>() {
            @Override public void call(Subscriber<? super StoreModel> subscriber) {
                subscriber.onError(e);
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    private void requestServer(Observer<Collection<StoreModel>> observer) {
        getLoadServiceObservable()
                .subscribe(onLoadObserver(observer));
    }

    private void loadFromDatabase(Observer<Collection<StoreModel>> observer) {
        getStoresObservable()
                .subscribe(getLoadObserver(observer));
    }

    public Subscription loadStores(Observer<Collection<StoreModel>> observer) {
        Subject<Collection<StoreModel>, Collection<StoreModel>> subject = new SerializedSubject<>(
                PublishSubject.<Collection<StoreModel>>create());
        Subscription subscribe = subject.subscribe(observer);

        loadFromDatabase(subject);

        requestServer(subject);

        return subscribe;
    }


    public Subscription heartBeat(
            StoreModel store,
            Observer<StoreModel> observer
    ) {
        Subject<StoreModel, StoreModel> subject = new SerializedSubject<>(PublishSubject.<StoreModel>create());
        Subscription subscription = subject.subscribe(observer);
        getLikeServiceObservable(store.getHashCode(), store.isLiked())
                .subscribe(onNewStoreReceivedObserver(store, subject));
        return subscription;
    }

    public Subscription viewed(
            StoreModel store,
            Observer<StoreModel> observer
    ) {
        Subject<StoreModel, StoreModel> subject = new SerializedSubject<>(PublishSubject.<StoreModel>create());
        Subscription subscription = subject.subscribe(observer);
        getViewServiceObservable(store.getHashCode()).subscribe(onNewStoreReceivedObserver(store, subject));
        return subscription;
    }

    public Subscription saveStore(
            StoreModel store,
            Observer<StoreModel> observer
    ) {
        Subject<StoreModel, StoreModel> subject = new SerializedSubject<>(PublishSubject.<StoreModel>create());
        Subscription subscription = subject.subscribe(observer);
        getSaveStoresObservable(store).subscribe(onSaveStoreObserver(subject));
        return subscription;
    }

    private Observer<? super StoreModel> onSaveStoreObserver(final Observer<StoreModel> observer) {
        return new Observer<StoreModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                getSaveErrorObservable(e).subscribe(observer);
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
                         .subscribeOn(AndroidSchedulers.mainThread())
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
                getSaveErrorObservable(ee).subscribe(observer);
            }

            @Override public void onNext(Integer integer) {
                L.i(StoresManager.class, "Successfully deleted from local database, the unsaved store on server.");
                getSaveErrorObservable(e).subscribe(observer);
            }
        };
    }

    private Observer<? super List<StoreModel>> getLoadObserver(final Observer<Collection<StoreModel>> observer) {
        return new Observer<List<StoreModel>>() {
            @Override public void onCompleted() {
                // Don't call observer's on completed method
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(List<StoreModel> storeModels) {
                observer.onNext(storeModels);
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
                getSaveErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(StoreProto.Store storeDto) {
                StoreModel newStore = StoreModel.instantiate(storeDto);
                mStoreDao.create(mBannerDao, oldStore, newStore)
                         .subscribeOn(AndroidSchedulers.mainThread())
                         .subscribe(onFinishSavingObserver(observer));
            }
        };
    }

    private Observer<? super StoreModel> onFinishSavingObserver(
            final Observer<StoreModel> observer
    ) {
        return new Observer<StoreModel>() {
            @Override public void onCompleted() {
                // Don't call observer's on completed method
            }

            @Override public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override public void onNext(StoreModel newStore) {
                observer.onNext(newStore);
            }
        };
    }


    private Observer<StoreProto.Stores> onLoadObserver(final Observer<Collection<StoreModel>> observer) {
        return new Observer<StoreProto.Stores>() {
            @Override public void onCompleted() {
                L.i(StoresManager.class, "Refresh request gets completed");
            }

            @Override public void onError(Throwable e) {
                L.w(StoresManager.class, "Refresh request gets error", e);
                getLoadErrorObservable(e).subscribe(observer);
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
                         .subscribeOn(AndroidSchedulers.mainThread())
                         .subscribe(onFinishObserver(observer));
            }
        };
    }

    private Observer<? super Collection<StoreModel>> onFinishObserver(
            final Observer<Collection<StoreModel>> observer
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
                observer.onNext(list);
            }
        };
    }


}
