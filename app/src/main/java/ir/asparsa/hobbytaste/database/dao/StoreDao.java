package ir.asparsa.hobbytaste.database.dao;

import ir.asparsa.hobbytaste.database.DatabaseHelper;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
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
                                        .eq(BannerModel.Columns.STORE_ID, model.getId())
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
}
