package ir.asparsa.hobbytaste.core.manager;

import com.j256.ormlite.dao.Dao;
import ir.asparsa.common.net.dto.StoreLightDto;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.database.dao.BannerDao;
import ir.asparsa.hobbytaste.database.dao.StoreDao;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.net.StoreService;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

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

    @Inject
    public StoresManager() {
    }

    public Observable<List<StoreModel>> getStores() {
        return mStoreDao
                .queryAllStores(mBannerDao)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public ConnectableObservable<Collection<StoreLightDto>> getRefreshable() {
        return mStoreService
                .loadStoreLightModels()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .share()
                .replay();
    }


    public Subscriber<Collection<StoreLightDto>> getObserver() {
        return new Subscriber<Collection<StoreLightDto>>() {
            @Override public void onCompleted() {
                L.i(StoresManager.class, "Refresh request gets completed");
            }

            @Override public void onError(Throwable e) {
                L.w(StoresManager.class, "Refresh request gets error", e);
            }

            @Override public void onNext(Collection<StoreLightDto> stores) {
                L.i(StoresManager.class, "Stores successfully received: " + stores);
                if (stores.size() == 0) {
                    return;
                }
                Collection<StoreModel> collection = new ArrayDeque<>();
                for (StoreLightDto store : stores) {
                    collection.add(StoreModel.instantiate(store));
                }
                mStoreDao.queryAllStores(mBannerDao).subscribe(removeOldBanners(collection));
            }
        };
    }

    private Observer<? super List<StoreModel>> removeOldBanners(final Collection<StoreModel> stores) {
        return new Observer<List<StoreModel>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(List<StoreModel> oldStores) {
                Collection<BannerModel> oldBanners = new ArrayDeque<>();
                for (StoreModel oldStore : oldStores) {
                    oldBanners.addAll(oldStore.getBanners());
                }
                mBannerDao.deleteAll(oldBanners).subscribe(removeOldStores(oldStores, stores));
            }
        };
    }

    private Observer<? super Integer> removeOldStores(
            final List<StoreModel> oldStores, final Collection<StoreModel> stores) {
        return new Observer<Integer>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Integer integer) {
                mStoreDao.deleteAll(oldStores).subscribe(addNewStores(stores));
            }
        };
    }

    private Observer<? super Integer> addNewStores(final Collection<StoreModel> stores) {
        final Collection<BannerModel> banners = new ArrayDeque<>();
        for (StoreModel store : stores) {
            banners.addAll(store.getBanners());
        }
        return new Observer<Integer>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {

            }

            @Override public void onNext(Integer integer) {
                mStoreDao.createAll(stores).subscribe(addNewBanners(banners));
            }
        };
    }

    private Observer<? super Collection<Dao.CreateOrUpdateStatus>> addNewBanners(
            final Collection<BannerModel> banners) {
        return new Observer<Collection<Dao.CreateOrUpdateStatus>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Collection<Dao.CreateOrUpdateStatus> statuses) {
                mBannerDao.createAll(banners).subscribe(finishAction());
            }
        };
    }

    private Observer<? super Collection<Dao.CreateOrUpdateStatus>> finishAction() {
        return new Observer<Collection<Dao.CreateOrUpdateStatus>>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Collection<Dao.CreateOrUpdateStatus> statuses) {
                L.i(StoresManager.class, "Stores completely saved");
            }
        };
    }
}
