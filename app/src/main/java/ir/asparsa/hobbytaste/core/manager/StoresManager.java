package ir.asparsa.hobbytaste.core.manager;

import ir.asparsa.hobbytaste.core.logger.L;
import ir.asparsa.hobbytaste.database.dao.StoreDao;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.net.StoreService;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

    public Observable<Collection<StoreModel>> getRefreshable() {
        return mStoreService
                .loadStoreModels()
                .retry()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observer<Collection<StoreModel>> getObserver() {
        return new Observer<Collection<StoreModel>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Collection<StoreModel> stores) {
                mStoreDao.queryForAll().subscribe(removeOldStores(stores));
            }
        };
    }

    private Action1<? super List<StoreModel>> removeOldStores(final Collection<StoreModel> stores) {
        return new Action1<List<StoreModel>>() {
            @Override public void call(List<StoreModel> oldStores) {
                Action1<? super Integer> onNext = addNewStores(stores, oldStores.size());
                for (StoreModel oldStore : oldStores) {
                    mStoreDao.delete(oldStore).subscribe(onNext);
                }
            }
        };
    }

    private Action1<? super Integer> addNewStores(final Collection<StoreModel> stores, final long count) {
        return new Action1<Integer>() {
            private int index = 0;

            @Override public void call(Integer integer) {
                if (index++ == count) {
                    Action1<? super Integer> onNext = finishAction(stores.size());
                    for (StoreModel store : stores) {
                        mStoreDao.create(store).subscribe(onNext);
                    }
                }
            }
        };
    }

    private Action1<? super Integer> finishAction(final int count) {
        return new Action1<Integer>() {
            private int index = 0;

            @Override public void call(Integer integer) {
                if (index++ == count) {
                    L.i(StoresManager.class, "Stores completely saved");
                }
            }
        };
    }


}
