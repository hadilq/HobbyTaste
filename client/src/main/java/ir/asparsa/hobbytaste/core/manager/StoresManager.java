package ir.asparsa.hobbytaste.core.manager;

import com.j256.ormlite.dao.Dao;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.StoreDto;
import ir.asparsa.hobbytaste.database.dao.BannerDao;
import ir.asparsa.hobbytaste.database.dao.StoreDao;
import ir.asparsa.hobbytaste.database.model.BannerModel;
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

    private Observable<Collection<StoreDto>> getServiceObservable() {
        return mStoreService
                .loadStoreModels()
                .retry(5)
                .subscribeOn(Schedulers.newThread());
    }

    private Observable<Collection<StoreModel>> getLoadErrorObservable(final Throwable e) {
        return Observable.create(new Observable.OnSubscribe<Collection<StoreModel>>() {
            @Override public void call(Subscriber<? super Collection<StoreModel>> subscriber) {
                subscriber.onError(e);
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    private void requestServer(Observer<Collection<StoreModel>> observer) {
        getServiceObservable().subscribe(onLoadObserver(observer));
    }

    private void loadFromDatabase(Observer<Collection<StoreModel>> observer) {
        getStoresObservable().subscribe(observer);
    }


    public Subscription loadStores(Observer<Collection<StoreModel>> observer) {

        Subject<Collection<StoreModel>, Collection<StoreModel>> subject = new SerializedSubject<>(
                PublishSubject.<Collection<StoreModel>>create());
        Subscription subscribe = subject.subscribe(observer);

        loadFromDatabase(subject);

        requestServer(subject);

        return subscribe;
    }

    private Observer<Collection<StoreDto>> onLoadObserver(final Observer<Collection<StoreModel>> observer) {
        return new Observer<Collection<StoreDto>>() {
            @Override public void onCompleted() {
                L.i(StoresManager.class, "Refresh request gets completed");
            }

            @Override public void onError(Throwable e) {
                L.w(StoresManager.class, "Refresh request gets error", e);
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(Collection<StoreDto> stores) {
                L.i(StoresManager.class, "Stores successfully received: " + stores);
                if (stores.size() == 0) {
                    return;
                }
                Collection<StoreModel> collection = new ArrayDeque<>();
                for (StoreDto store : stores) {
                    collection.add(StoreModel.instantiate(store));
                }
                mStoreDao.queryAllStores(mBannerDao).subscribe(onRemoveOldBannersObserver(collection, observer));
            }
        };
    }

    private Observer<? super List<StoreModel>> onRemoveOldBannersObserver(
            final Collection<StoreModel> stores,
            final Observer<Collection<StoreModel>> observer
    ) {

        return new Observer<List<StoreModel>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.i(StoresManager.class, "Stores cannot remove old banners", e);
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(List<StoreModel> oldStores) {
                Collection<BannerModel> oldBanners = new ArrayDeque<>();
                for (StoreModel oldStore : oldStores) {
                    oldBanners.addAll(oldStore.getBanners());
                }
                mBannerDao.deleteAll(oldBanners).subscribe(onRemoveOldStoresObserver(oldStores, stores, observer));
            }
        };
    }

    private Observer<? super Integer> onRemoveOldStoresObserver(
            final List<StoreModel> oldStores,
            final Collection<StoreModel> stores,
            final Observer<Collection<StoreModel>> observer
    ) {
        return new Observer<Integer>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.i(StoresManager.class, "Stores cannot remove old stores", e);
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(Integer integer) {
                mStoreDao.deleteAll(oldStores).subscribe(onAddNewStoresObserver(stores, observer));
            }
        };
    }

    private Observer<? super Integer> onAddNewStoresObserver(
            final Collection<StoreModel> stores,
            final Observer<Collection<StoreModel>> observer
    ) {
        return new Observer<Integer>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.i(StoresManager.class, "Stores cannot add new stores", e);
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(Integer integer) {
                Collection<BannerModel> banners = new ArrayDeque<>();
                for (StoreModel store : stores) {
                    banners.addAll(store.getBanners());
                }
                mStoreDao.createAll(stores).subscribe(onAddNewBannersObserver(banners, observer));
            }
        };
    }

    private Observer<? super Collection<Dao.CreateOrUpdateStatus>> onAddNewBannersObserver(
            final Collection<BannerModel> banners,
            final Observer<Collection<StoreModel>> observer
    ) {
        return new Observer<Collection<Dao.CreateOrUpdateStatus>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.i(StoresManager.class, "Stores cannot add new banners", e);
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(Collection<Dao.CreateOrUpdateStatus> statuses) {
                mBannerDao.createAll(banners).subscribe(onFinishObserver(observer));
            }
        };
    }

    private Observer<? super Collection<Dao.CreateOrUpdateStatus>> onFinishObserver(
            final Observer<Collection<StoreModel>> observer
    ) {
        return new Observer<Collection<Dao.CreateOrUpdateStatus>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.i(StoresManager.class, "Stores cannot finish action", e);
                getLoadErrorObservable(e).subscribe(observer);
            }

            @Override public void onNext(Collection<Dao.CreateOrUpdateStatus> statuses) {
                L.i(StoresManager.class, "Stores completely saved");
                getStoresObservable().subscribe(observer);
            }
        };
    }
}
