package ir.asparsa.hobbytaste.core.manager;

import ir.asparsa.hobbytaste.core.logger.L;
import ir.asparsa.hobbytaste.database.dao.StoreDao;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.net.StoreService;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Singleton;
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
    public StoresManager() {
    }

    public Observable<List<StoreModel>> getStores() {
        return mStoreDao
                .queryForAll()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public ConnectableObservable<Collection<StoreModel>> getRefreshable() {
        return mStoreService
                .loadStoreModels()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .share()
                .replay();
    }


    public Subscriber<Collection<StoreModel>> getObserver() {
        return new Subscriber<Collection<StoreModel>>() {
            @Override public void onCompleted() {
                L.i(StoresManager.class, "Refresh request gets completed");
            }

            @Override public void onError(Throwable e) {
                L.w(StoresManager.class, "Refresh request gets error", e);
            }

            @Override public void onNext(Collection<StoreModel> stores) {
                L.i(StoresManager.class, "Stores successfully received: " + stores);
                if (stores.size() == 0) {
                    return;
                }
                mStoreDao.queryForAll().subscribe(removeOldStores(stores));
            }
        };
    }

    private Action1<? super List<StoreModel>> removeOldStores(final Collection<StoreModel> stores) {
        return new Action1<List<StoreModel>>() {
            @Override public void call(List<StoreModel> oldStores) {
                mStoreDao.deleteAll(oldStores).subscribe(addNewStores(stores));
            }
        };
    }

    private Action1<? super Integer> addNewStores(final Collection<StoreModel> stores) {
        return new Action1<Integer>() {
            @Override public void call(Integer integer) {
                mStoreDao.createAll(stores).subscribe(finishAction());
            }
        };
    }

    private Action1<? super Integer> finishAction() {
        return new Action1<Integer>() {
            @Override public void call(Integer integer) {
                L.i(StoresManager.class, "Stores completely saved");
            }
        };
    }
}
