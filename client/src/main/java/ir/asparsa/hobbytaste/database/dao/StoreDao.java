package ir.asparsa.hobbytaste.database.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ir.asparsa.common.database.model.Banner;
import ir.asparsa.common.database.model.Store;
import ir.asparsa.common.util.MapUtil;
import ir.asparsa.hobbytaste.database.DatabaseHelper;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import rx.Observable;
import rx.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.*;

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

    public Observable<Stores> queryStores(
            final double latitude,
            final double longitude,
            final int offset,
            final int limit,
            final BannerDao bannerDao
    ) {
        return Observable.create(new Observable.OnSubscribe<Stores>() {
            @Override public void call(Subscriber<? super Stores> subscriber) {
                try {

                    long countOf = getDao().countOf();
                    if (countOf < offset) {
                        subscriber.onNext(new Stores(new ArrayList<StoreModel>(), countOf));
                        subscriber.onCompleted();
                        return;
                    }

                    List<StoreHolder> holders = new ArrayList<>();
                    for (StoreModel model : getDao()) {
                        float distance = MapUtil.distFrom(latitude, longitude, model.getLat(), model.getLon());
                        holders.add(new StoreHolder(model, distance));
                    }

                    Collections.sort(holders, new Comparator<StoreHolder>() {
                        @Override public int compare(
                                StoreHolder o1,
                                StoreHolder o2
                        ) {
                            return o1.distance == o2.distance ? 0 : (o1.distance > o2.distance ? 1 : -1);
                        }
                    });

                    List<StoreModel> models = new ArrayList<>();
                    for (StoreHolder holder : holders.subList(offset, Math.min(offset + limit, holders.size()))) {
                        holder.storeModel.setBanners(bannerDao.getDao().query(
                                bannerDao.getDao()
                                         .queryBuilder()
                                         .where()
                                         .eq(Banner.Columns.STORE, holder.storeModel.getId())
                                         .prepare()));

                        models.add(holder.storeModel);
                    }

                    subscriber.onNext(new Stores(models, countOf));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Integer> delete(
            final BannerDao bannerDao,
            final StoreModel data
    ) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                try {
                    if (data.getBanners() != null && data.getBanners().size() != 0) {
                        for (BannerModel banner : data.getBanners()) {
                            bannerDao.getDao().delete(banner);
                        }
                    }
                    subscriber.onNext(getDao().delete(data));
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
                            .eq(Store.Columns.HASH_CODE, newModel.getHashCode())
                            .prepare());
        } else {
            if (oldModel.getId() == null) {
                throw new SQLDataException("Id of old model is null " + oldModel);
            }
            list = new ArrayList<>();
            list.add(oldModel);
        }

        if (list.isEmpty()) {
            newModel.setId(null);
            newModel = getDao().createIfNotExists(newModel);
            for (BannerModel newBanner : newModel.getBanners()) {
                newBanner.setStoreId(newModel.getId());
                bannerDao.getDao().create(newBanner);
            }
        } else {
            newModel.setId(list.get(0).getId());
            getDao().update(newModel);
            for (BannerModel bannerModel : newModel.getBanners()) {
                bannerModel.setStoreId(newModel.getId());
            }

            List<BannerModel> oldBannerList;
            if (oldModel == null) {
                oldBannerList = bannerDao.getDao().query(
                        bannerDao.getDao().queryBuilder()
                                 .where()
                                 .eq(Banner.Columns.STORE, newModel.getId())
                                 .prepare());
            } else {
                oldBannerList = oldModel.getBanners();
                for (BannerModel bannerModel : oldBannerList) {
                    bannerModel.setStoreId(newModel.getId());
                }
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

    public Observable<StoreModel> findByHashCode(final long storeHashCode) {
        return Observable.create(new Observable.OnSubscribe<StoreModel>() {
            @Override public void call(Subscriber<? super StoreModel> subscriber) {
                try {
                    List<StoreModel> stores = getDao().query(
                            getDao().queryBuilder()
                                    .where()
                                    .eq(Store.Columns.HASH_CODE, storeHashCode)
                                    .prepare());
                    subscriber.onNext(stores.size() > 0 ? stores.get(0) : null);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static class StoreHolder {
        StoreModel storeModel;
        float distance;

        StoreHolder(
                StoreModel storeModel,
                float distance
        ) {
            this.storeModel = storeModel;
            this.distance = distance;
        }
    }

    public static class Stores {
        private List<StoreModel> stores;
        private long totalElements;

        Stores(
                List<StoreModel> stores,
                long totalElement
        ) {
            this.stores = stores;
            this.totalElements = totalElement;
        }

        public List<StoreModel> getStores() {
            return stores;
        }

        public long getTotalElements() {
            return totalElements;
        }
    }

}
