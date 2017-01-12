package ir.asparsa.hobbytaste.database.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ir.asparsa.common.database.model.Banner;
import ir.asparsa.common.database.model.Store;
import ir.asparsa.hobbytaste.database.DatabaseHelper;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import rx.Observable;
import rx.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Singleton
public class StoreDao extends AbsDao<StoreModel, Integer> {

    @Inject
    public StoreDao(DatabaseHelper mDatabaseHelper) {
        super(mDatabaseHelper);
    }

    @Override protected Class<StoreModel> getModel() {
        return StoreModel.class;
    }

    public Observable<List<StoreModel>> queryAllStores(final BannerDao bannerDao) {
        return Observable.create(new Observable.OnSubscribe<List<StoreModel>>() {
            @Override public void call(Subscriber<? super List<StoreModel>> subscriber) {
                try {
                    List<StoreModel> models = getDao().queryForAll();
                    for (StoreModel model : models) {
                        model.setBanners(bannerDao.getDao().query(
                                bannerDao.getDao()
                                         .queryBuilder()
                                         .where()
                                         .eq(Banner.Columns.STORE, model.getId())
                                         .prepare()));

                    }
                    subscriber.onNext(models);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<StoreModel> create(
            @NonNull final BannerDao bannerDao,
            @Nullable final StoreModel oldModel,
            @NonNull final StoreModel newModel
    ) {
        return Observable.create(new Observable.OnSubscribe<StoreModel>() {
            @Override public void call(Subscriber<? super StoreModel> subscriber) {
                try {
                    createStore(bannerDao, oldModel, newModel);

                    subscriber.onNext(newModel);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Collection<StoreModel>> createAll(
            @NonNull final BannerDao bannerDao,
            @NonNull final Collection<StoreModel> newModels
    ) {
        return Observable.create(new Observable.OnSubscribe<Collection<StoreModel>>() {
            @Override public void call(Subscriber<? super Collection<StoreModel>> subscriber) {
                try {
                    for (StoreModel newModel : newModels) {
                        createStore(bannerDao, null, newModel);
                    }

                    subscriber.onNext(newModels);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });

    }

    private void createStore(
            @NonNull BannerDao bannerDao,
            @Nullable StoreModel oldModel,
            @NonNull StoreModel newModel
    ) throws java.sql.SQLException {
        List<StoreModel> list;
        if (oldModel == null) {
            list = getDao().query(
                    getDao().queryBuilder()
                            .where()
                            .eq(Store.Columns.ID, newModel.getId())
                            .prepare());
        } else {
            list = new ArrayList<>();
            list.add(oldModel);
        }

        getDao().createOrUpdate(newModel);
        if (list.isEmpty()) {
            for (BannerModel newBanner : newModel.getBanners()) {
                bannerDao.getDao().create(newBanner);
            }
        } else {
            List<BannerModel> oldBannerList;
            if (oldModel == null) {
                oldBannerList = bannerDao.getDao().query(
                        bannerDao.getDao().queryBuilder()
                                 .where()
                                 .eq(Banner.Columns.STORE, newModel.getId())
                                 .prepare());
            } else {
                oldBannerList = oldModel.getBanners();
            }
            for (BannerModel oldBanner : oldBannerList) {
                boolean isFound = false;
                for (BannerModel newBanner : newModel.getBanners()) {
                    if (oldBanner.equals(newBanner)) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    bannerDao.getDao().delete(oldBanner);
                }
            }

            for (BannerModel newBanner : newModel.getBanners()) {
                boolean isFound = false;
                for (BannerModel oldBanner : oldBannerList) {
                    if (oldBanner.equals(newBanner)) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    bannerDao.getDao().create(newBanner);
                }
            }
        }
    }
}
